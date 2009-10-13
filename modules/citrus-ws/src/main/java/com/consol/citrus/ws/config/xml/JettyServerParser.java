/*
 * Copyright 2006-2009 ConSol* Software GmbH.
 * 
 * This file is part of Citrus.
 * 
 *  Citrus is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Citrus is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Citrus.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.consol.citrus.ws.config.xml;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class JettyServerParser extends AbstractBeanDefinitionParser {

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
            .genericBeanDefinition("com.consol.citrus.ws.JettyServer");
        
        
        String port = element.getAttribute(WSParserConstants.PORT_ATTRIBUTE);
        
        if (StringUtils.hasText(port)) {
            builder.addPropertyValue(WSParserConstants.PORT_PROPERTY, port);
        }
        
        String autoStart = element.getAttribute(WSParserConstants.AUTOSTART_ATTRIBUTE);
        
        if (StringUtils.hasText(autoStart)) {
            builder.addPropertyValue(WSParserConstants.AUTOSTART_PROPERTY, autoStart);
        }
        
        String resourceBase = element.getAttribute(WSParserConstants.RESOURCE_BASE_ATTRIBUTE);
        
        if (StringUtils.hasText(resourceBase)) {
            builder.addPropertyValue(WSParserConstants.RESOURCE_BASE_PROPERTY, resourceBase);
        }
        
        String contextConfigLocation = element.getAttribute(WSParserConstants.CONTEXT_CONFIC_LOCATION_ATTRIBUTE);
        
        if (StringUtils.hasText(contextConfigLocation)) {
            builder.addPropertyValue(WSParserConstants.CONTEXT_CONFIC_LOCATION_PROPERTY, contextConfigLocation);
        }
        
        return builder.getBeanDefinition();
    }
}