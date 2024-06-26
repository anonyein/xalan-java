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

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan3.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of the remove() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncRemove extends Function2Args {

    private static final long serialVersionUID = -8240867333953131047L;

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
        
        ResultSequence result = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        try {
            XObject xObject0 = m_arg0.execute(xctxt);
            XObject xObject1 = m_arg1.execute(xctxt);
            
            ResultSequence rsArg0 = XslTransformEvaluationHelper.getResultSequenceFromXObject(
                                                                                          xObject0, xctxt);
            
            int seqRemovePos = getSequenceRemovePosition(xObject1);
            
            if (seqRemovePos < 1 || (seqRemovePos > rsArg0.size())) {
               for (int idx = 0; idx < rsArg0.size(); idx++) {
                  result.add(rsArg0.item(idx));  
               }
            }
            else if (rsArg0.size() == 0) {
               return result;    
            }
            else {
               for (int idx = 0; idx < (seqRemovePos - 1); idx++) {
                  result.add(rsArg0.item(idx));    
               }
               
               for (int idx = seqRemovePos; idx < rsArg0.size(); idx++) {
                  result.add(rsArg0.item(idx));    
               }
            }
        }
        catch (TransformerException ex) {
            throw new TransformerException(ex.getMessage(), srcLocator);  
        }
        
        return result;
    }
    
    /**
     * Given an XObject object instance (representing the second argument [the 'remove'
     * position argument] of function call fn:remove), get its value as an integer.  
     */
    private int getSequenceRemovePosition(XObject xObject) throws TransformerException {
       
       int seqRemovePos = -1;
       
       if (xObject instanceof XNumber) {
          double dbl = ((XNumber)xObject).num();
          if (dbl == (int)dbl) {
             seqRemovePos = (int)dbl;  
          }
          else {
             throw new TransformerException("FORG0006 : Incorrect value " + dbl + " provided to second argument "
                                                                              + "of function fn:remove. This argument value "
                                                                              + "needs to be an integer."); 
          }
       }
       else if (xObject instanceof XSNumericType) {
          String argStrVal = ((XSNumericType)xObject).stringValue();
          double dbl = (Double.valueOf(argStrVal)).doubleValue();
          if (dbl == (int)dbl) {
             seqRemovePos = (int)dbl;  
          }
          else {
             throw new TransformerException("FORG0006 : Incorrect value " + dbl + " provided to second argument "
                                                                              + "of function fn:remove. This argument value "
                                                                              + "needs to be an integer.");  
          }
       }
       else {
          throw new TransformerException("FORG0006 : The second argument to function fn:remove, needs to be "
                                                                                                         + "an integer value");  
       }
       
       return seqRemovePos; 
    }

}
