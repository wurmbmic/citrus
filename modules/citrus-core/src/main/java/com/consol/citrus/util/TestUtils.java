/*
 * Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.util;

import java.util.*;

import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import com.consol.citrus.TestAction;
import com.consol.citrus.TestCase;
import com.consol.citrus.container.TestActionContainer;
import com.consol.citrus.report.FailureStackElement;

/**
 * Utility class for test cases providing several utility 
 * methods regarding Citrus test cases.
 * 
 * @author Christoph Deppisch
 */
public abstract class TestUtils {

    /**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger(TestUtils.class);
    
    /**
     * Prevent instantiation.
     */
    private TestUtils() {
    }
    
    /**
     * 
     * @param test
     * @return
     */
    public static List<FailureStackElement> getFailureStack(final TestCase test) {
        final List<FailureStackElement> failureStack = new ArrayList<FailureStackElement>();
        
        try {
            final String testFilePath = test.getPackageName().replace('.', '/') + "/" + test.getName();

            Resource testFileResource = new ClassPathResource(testFilePath + ".xml");
            if (!testFileResource.exists()) {
                return failureStack;
            }
            
            // first check if test failed during setup
            if (test.getLastExecutedAction() == null) {
                failureStack.add(new FailureStackElement(testFilePath, "init", 0L));
                // no actions were executed yet failure caused by test setup: abort
                return failureStack;
            }
            
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader reader = factory.newSAXParser().getXMLReader();
            
            reader.setContentHandler(new FailureStackContentHandler(failureStack, test, testFilePath));
            
            reader.parse(new InputSource(testFileResource.getInputStream()));
        } catch (RuntimeException e) {
            log.warn("Failed to locate line numbers for failure stack trace", e);
        } catch (Exception e) {
            log.warn("Failed to locate line numbers for failure stack trace", e);
        }
        
        return failureStack;
    }
    
    /**
     * Special content handler responsible of filling the failure stack.
     */
    private static final class FailureStackContentHandler extends DefaultHandler {
        /** The failure stack to work on */
        private final List<FailureStackElement> failureStack;
        /** The actual test case */
        private final TestCase test;
        /** The test file path */
        private final String testFilePath;
        /** Locator providing actual line number information */
        private Locator locator;
        /** Failure stack finder */
        private FailureStackFinder stackFinder;
        /** Start/stop to listen for error line ending */
        private boolean findLineEnding = false;
        /** The name of action which caused the error */
        private String failedActionName;

        /**
         * Default constructor using fields.
         * @param failureStack
         * @param test
         * @param testFilePath
         */
        private FailureStackContentHandler(List<FailureStackElement> failureStack, 
                                           TestCase test,
                                           String testFilePath) {
            this.failureStack = failureStack;
            this.test = test;
            this.testFilePath = testFilePath;
        }

        @Override
        public void startElement(String uri, String localName,
                String qName, Attributes attributes)
                throws SAXException {
            
            //start when actions element is reached
            if (qName.equals("actions")) {
                stackFinder = new FailureStackFinder(test);
                return;
            }
            
            if (stackFinder != null && stackFinder.isFailureStackElement(qName)) {
                failureStack.add(new FailureStackElement(testFilePath, qName, Long.valueOf(locator.getLineNumber())));
                
                if (stackFinder.getNestedActionContainer() != null && 
                        stackFinder.getNestedActionContainer().getLastExecutedAction() != null) {
                    //continue with nested action container, in order to find out which action caused the failure
                    stackFinder = new FailureStackFinder(stackFinder.getNestedActionContainer());
                } else {
                    //stop failure stack evaluation as failure-causing action was found
                    stackFinder = null;
                    
                    //now start to find ending line number
                    findLineEnding = true;
                    failedActionName = qName;
                }
            }
            
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (findLineEnding && qName.equals(failedActionName)) {
                // get last failure stack element
                FailureStackElement failureStackElement = failureStack.get(failureStack.size()-1);
                failureStackElement.setLineNumberEnd(Long.valueOf(locator.getLineNumber()));
                findLineEnding = false;
            }
            super.endElement(uri, localName, qName);
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }
    }

    /**
     * Failure stack finder listens for actions in a testcase 
     */
    private static class FailureStackFinder {
        /** Action list */
        private Stack<TestAction> actionStack = new Stack<TestAction>();
        
        /** Test action we are currently working on */
        private TestAction action = null;
        
        /**
         * Default constructor using fields.
         * @param container
         */
        public FailureStackFinder(TestActionContainer container) {
            int lastActionIndex = container.getActionIndex(container.getLastExecutedAction());
            
            for (int i = lastActionIndex; i >= 0; i--) {
                actionStack.add(container.getActions().get(i));
            }
        }

        /**
         * Checks whether the target action is reached within the action container.
         * Method counts the actions inside the action container and waits for the target index
         * to be reached.
         * 
         * @param eventElement actual action name, can also be a nested element in the XML DOM tree so check name before evaluation
         * @return boolean flag to mark that target action is reached or not
         */
        public boolean isFailureStackElement(String eventElement) {
            if (action == null) {
                action = actionStack.pop();
            }
        
            /* filter method calls that actually are based on other elements within the DOM
             * tree. SAX content handler can not differ between action elements and other nested elements
             * in startElement event. 
             */
            if (eventElement.equals(action.getName())) {
                if (action instanceof TestActionContainer && !actionStack.isEmpty()) {
                    TestActionContainer container = (TestActionContainer)action;
                    for (int i = container.getActions().size()-1; i >= 0; i--) {
                        actionStack.add(container.getActions().get(i));
                    }
                }
                
                if (!actionStack.isEmpty()) {
                    action = null;
                }
            } else {
                return false;
            }
        
            return actionStack.isEmpty();
        }
        
        /**
         * Is target action a container itself? If yes the stack evaluation should
         * continue with nested container, in order to get nested action that caused the failure.
         * 
         * @return the nested container or null
         */
        public TestActionContainer getNestedActionContainer() {
            if (action instanceof TestActionContainer) {
                return (TestActionContainer)action;
            } else {
                return null;
            }
        }
    }
}
