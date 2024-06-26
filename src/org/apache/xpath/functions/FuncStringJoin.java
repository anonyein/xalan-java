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

import javax.xml.transform.SourceLocator;

import org.apache.xalan3.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.Range;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Execute the string-join() function.
 * 
 * This function returns a string created by concatenating the items 
 * in a sequence, with a defined separator between adjacent items.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncStringJoin extends Function2Args {

   private static final long serialVersionUID = 4171534319684252331L;

   /**
   * Execute the function. The function must return
   * a valid object.
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
    
    ResultSequence arg0ResultSeq = null;
    
    final int contextNode = xctxt.getCurrentNode();
    
    if (m_arg0 instanceof Function) {
        XObject evalResult = ((Function)m_arg0).execute(xctxt);
        if (evalResult instanceof ResultSequence) {
           arg0ResultSeq = (ResultSequence)evalResult;   
        }                
    }    
    else if (m_arg0 instanceof Variable) {
        XObject evalResult = ((Variable)m_arg0).execute(xctxt);
        if (evalResult instanceof ResultSequence) {
           arg0ResultSeq = (ResultSequence)evalResult;    
        }
    }
    else if (m_arg0 instanceof Range) {
        XObject evalResult = ((Range)m_arg0).execute(xctxt);
        if (evalResult instanceof ResultSequence) {
           arg0ResultSeq = (ResultSequence)evalResult;    
        }
    }
    else if (m_arg0 instanceof LocPathIterator) {
        arg0ResultSeq = new ResultSequence();
        
        DTMManager dtmMgr = (DTMManager)xctxt;        
        DTMIterator arg0DtmIterator = m_arg0.asIterator(xctxt, contextNode);        
        
        int nextNodeDtmHandle;
        
        while ((nextNodeDtmHandle = arg0DtmIterator.nextNode()) != DTM.NULL) {
            XNodeSet xNodeSetItem = new XNodeSet(nextNodeDtmHandle, dtmMgr);            
            String nodeStrValue = xNodeSetItem.str();
            
            DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);
            
            if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
               XSUntyped xsUntyped = new XSUntyped(nodeStrValue);
               arg0ResultSeq.add(xsUntyped);
            }
            else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
               XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
               arg0ResultSeq.add(xsUntypedAtomic);
            }
            else {
               XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
               arg0ResultSeq.add(xsUntypedAtomic);
            }                        
        }
    }
    else if (m_arg0 instanceof Operation) {
        arg0ResultSeq = new ResultSequence();
        
        XObject evalResult = ((Operation)m_arg0).execute(xctxt);
        if (evalResult instanceof ResultSequence) {
           ResultSequence resultSeq = (ResultSequence)evalResult;
           for (int idx = 0; idx < resultSeq.size(); idx++) {
              arg0ResultSeq.add(resultSeq.item(idx));  
           }
        }
    }
    
    if (arg0ResultSeq == null) {
        throw new javax.xml.transform.TransformerException("The first argument of fn:string-join, "
                                                              + "did not evaluate to a sequence.", 
                                                                             srcLocator);    
    }
    
    String strJoinSeparator = null;
    
    if (m_arg1 == null) {
       strJoinSeparator = "";   
    }    
    else if (m_arg1 instanceof XString) {
       strJoinSeparator = ((XString)m_arg1).str();
    }
    else {
       throw new javax.xml.transform.TransformerException("The second argument of fn:string-join must "
                                                               + "be absent, or it must be a string value.", 
                                                                                     srcLocator);
    }
    
    StringBuffer strBuffer = new StringBuffer();
    
    for (int idx = 0; idx < arg0ResultSeq.size(); idx++) {
       String strValue = null;
       
       XObject xObject = arg0ResultSeq.item(idx);
       if (xObject instanceof XSUntyped) {
          strValue = ((XSUntyped)xObject).stringValue();     
       }
       else if (xObject instanceof XSUntypedAtomic) {
          strValue = ((XSUntypedAtomic)xObject).stringValue();  
       }
       else if (xObject instanceof XSAnyType) {
          strValue = ((XSAnyType)xObject).stringValue(); 
       }
       else {
          strValue = xObject.str(); 
       }
       
       if (idx < (arg0ResultSeq.size() - 1)) {
          strBuffer.append(strValue + strJoinSeparator);    
       }
       else {
          strBuffer.append(strValue);    
       }
    }        

    return new XString(strBuffer.toString());
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
     if (argNum < 1 || argNum > 2) {
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
