/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
/*
 * $Id$
 */

package org.apache.xalan3.xsltc.runtime;

import org.apache.xalan3.xsltc.DOM;
import org.xml.sax.AttributeList;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
public final class Attributes implements AttributeList {
    private int _element;
    private DOM _document;

    public Attributes(DOM document, int element) {
	_element = element;
	_document = document;
    }

    public int getLength() {
	return 0;
    }

    public String getName(int i) {
	return null;
    }

    public String getType(int i) {
	return null;
    }

    public String getType(String name) {
	return null;
    }

    public String getValue(int i) {
	return null;
    }

    public String getValue(String name) {
	return null;
    }
}
