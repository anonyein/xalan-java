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
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Execute the distinct-values() function.
 * 
 * XPath 3.1 F&O spec, defines the function fn:distinct-values as follows,
 *  
 * This function returns the values that appear in a sequence, with 
 * duplicates eliminated.
 * 
 * Function signatures :
 *      1) fn:distinct-values($arg as xs:anyAtomicType*) as xs:anyAtomicType*

        2) fn:distinct-values($arg as xs:anyAtomicType*, $collation as xs:string) 
                                                                            as xs:anyAtomicType*
                                                                                                                                                        
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDistinctValues extends Function2Args {
    
   private static final long serialVersionUID = -1637800188441824456L;
    
   private static final String FUNCTION_NAME = "distinct-values()"; 

  /**
   * Execute the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * 
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {      
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        ResultSequence resultSeq = new ResultSequence();
        
        Expression arg0 = getArg0();        
        Expression arg1 = getArg1();   // the second argument of this function, is unused for now
        
        XObject arg0Obj = arg0.execute(xctxt);
        
        if (arg0Obj instanceof XNodeSet) {
           DTMManager dtmMgr = (DTMManager)xctxt;
           
           XNodeSet xNodeSet = (XNodeSet)arg0Obj;           
           DTMIterator sourceNodes = xNodeSet.iter();
           
           int nextNodeDtmHandle;
           
           while ((nextNodeDtmHandle = sourceNodes.nextNode()) != DTM.NULL) {
              XNodeSet xNodeSetItem = new XNodeSet(nextNodeDtmHandle, dtmMgr);
              String nodeStrValue = xNodeSetItem.str();
              
              DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);
              
              if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                 XSUntyped xsUntyped = new XSUntyped(nodeStrValue);                 
                 XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                           xsUntyped, true);
              }
              else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                 XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                 XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                           xsUntypedAtomic, true);
              }
              else {
                 XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                 XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                           xsUntypedAtomic, true);
              }
           }
        }
        else if (arg0Obj instanceof ResultSequence) {
           ResultSequence inpResultSeq = (ResultSequence)arg0Obj; 
           for (int idx = 0; idx < inpResultSeq.size(); idx++) {
              XObject xObj = inpResultSeq.item(idx);
              if (xObj instanceof XSAnyType) {
                 XSAnyType xsAnyType = (XSAnyType)xObj;
                 XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                             xsAnyType, true);
              }
              else {
                  XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                             xObj, true);
              }
           }
        }
        else {
           // we're assuming here that, an input value is an 
           // xdm singleton item.            
           if (arg0Obj instanceof XSAnyType) {
              XSAnyType xsAnyType = (XSAnyType)arg0Obj;
              XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                          xsAnyType, false);
           }
           else {
              String seqItemStrValue = arg0Obj.str();
              XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                        new XString(seqItemStrValue), false);
           }
        }
            
        return resultSeq;
  }

/**
   * Check that the number of arguments passed to this function is correct.
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
     if (!(argNum == 1 || argNum == 2)) {
        reportWrongNumberArgs();
     }
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                              XPATHErrorResources.ER_ONE_OR_TWO, null)); //"1 or 2"
  }

}
