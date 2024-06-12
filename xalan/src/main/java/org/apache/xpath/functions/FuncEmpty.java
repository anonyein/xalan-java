/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.functions;

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of the empty() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncEmpty extends FunctionOneArg {
    
    private static final long serialVersionUID = 8023120626582040786L;

    /**
     * Execute the function. The function must return a valid object.
     * 
     * @param xctxt The current execution context.
     * @return A valid XObject.
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        
        XObject result = null;
        
        XObject xObject0 = m_arg0.execute(xctxt);
        
        if (xObject0 instanceof XNodeSet) {
           XNodeSet nodeSet = (XNodeSet)xObject0;
           if (nodeSet.getLength() == 0) {
              result = XBoolean.S_TRUE;  
           }
           else {
              result = XBoolean.S_FALSE; 
           }
        }
        else if (xObject0 instanceof ResultSequence) {
           ResultSequence resultSeq = (ResultSequence)xObject0;
           if (resultSeq.size() == 0) {
              result = XBoolean.S_TRUE; 
           }
           else {
              result = XBoolean.S_FALSE;
           }
        }
        else {
           result = XBoolean.S_FALSE; 
        }
        
        return result;
    }

}
