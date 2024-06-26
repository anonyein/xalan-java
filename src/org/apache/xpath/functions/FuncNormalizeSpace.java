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
package org.apache.xpath.functions;

import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.xml.sax.ContentHandler;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 fn:normalize-space function.
 * 
 * @xsl.usage advanced
 */
public class FuncNormalizeSpace extends FunctionDef1Arg
{
    static final long serialVersionUID = -3377956872032190880L;

  /**
   * Implementation of the function. The function must return
   * a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
      XMLString s1 = getArg0AsString(xctxt);
    
      XMLString xmlStr = s1.fixWhiteSpace(true, true, false);

      return new XSString(xmlStr.toString());
  }
  
  /**
   * Execute an expression in the XPath runtime context, and return the 
   * result of the expression.
   *
   *
   * @param xctxt The XPath runtime context.
   *
   * @return The result of the expression in the form of a <code>XObject</code>.
   *
   * @throws javax.xml.transform.TransformerException if a runtime exception 
   *         occurs.
   */
  public void executeCharsToContentHandler(XPathContext xctxt, 
                                              ContentHandler handler)
    throws javax.xml.transform.TransformerException,
           org.xml.sax.SAXException
  {
    if(Arg0IsNodesetExpr())
    {
      int node = getArg0AsNode(xctxt);
      if(DTM.NULL != node)
      {
        DTM dtm = xctxt.getDTM(node);
        dtm.dispatchCharactersEvents(node, handler, true);
      }
    }
    else
    {
      XObject obj = execute(xctxt);
      obj.dispatchCharactersEvents(handler);
    }
  }

}
