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
package org.apache.xalan.templates;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.xs.types.XSBoolean;
import org.apache.xpath.xs.types.XSDate;
import org.apache.xpath.xs.types.XSDateTime;
import org.apache.xpath.xs.types.XSDayTimeDuration;
import org.apache.xpath.xs.types.XSDecimal;
import org.apache.xpath.xs.types.XSDouble;
import org.apache.xpath.xs.types.XSDuration;
import org.apache.xpath.xs.types.XSFloat;
import org.apache.xpath.xs.types.XSInt;
import org.apache.xpath.xs.types.XSInteger;
import org.apache.xpath.xs.types.XSLong;
import org.apache.xpath.xs.types.XSString;
import org.apache.xpath.xs.types.XSYearMonthDuration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Implementation of XSLT xsl:function element.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#stylesheet-functions
 *
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemFunction extends ElemTemplate
{

  private static final long serialVersionUID = 4973132678982467288L;
  
  /**
   * The value of the "name" attribute.
   */
  private static QName m_name;

  /**
   * Class constructor.
   */
  public ElemFunction() {}

  /**
   * Set the value of xsl:function's "name" 
   * attribute.
   */
  public void setName(QName qName)
  {      
      super.setName(qName);
      m_name = qName;
  }

  /**
   * Get the value of xsl:function's "name" 
   * attribute. 
   */
  public QName getName()
  {
      return super.getName();
  }

  /**
   * Get an integer representation of the element type.
   *
   * @return An integer representation of the element, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
     return Constants.ELEMNAME_FUNCTION;
  }

  /**
   * Return the node name.
   *
   * @return The node name
   */
  public String getNodeName()
  {
     return Constants.ELEMNAME_FUNCTION_STRING;
  }

  /**
   * This method evaluates the xsl:function call, and returns the result
   * to the caller of this function.
   */
  public XObject executeXslFunction(TransformerImpl transformer, 
                                                             ResultSequence argSequence) throws TransformerException {
      XObject result = null;
      
      XPathContext xctxt = transformer.getXPathContext();
      
      SourceLocator srcLocator = xctxt.getSAXLocator(); 
      
      VariableStack varStack = xctxt.getVarStack();
      
      int thisframe = varStack.getStackFrame();
      int nextFrame = varStack.link(m_frameSize);
      
      varStack.setStackFrame(thisframe);
      
      String funcLocalName = m_name.getLocalName();
      String funcNameSpaceUri = m_name.getNamespaceURI(); 
      
      int paramCount = 0;
      for (ElemTemplateElement elem = getFirstChildElem(); elem != null; 
                                                              elem = elem.getNextSiblingElem()) {
          if (elem.getXSLToken() == Constants.ELEMNAME_PARAMVARIABLE) {
             if (argSequence.size() < (paramCount + 1)) {
                 throw new TransformerException("XPST0017 : Cannot find a " + argSequence.size() + " argument function named "
                                                                            + "{" + funcNameSpaceUri + "}" + funcLocalName + "() within a stylesheet scope. "
                                                                            + "The function name was recognized, but number of arguments is wrong.", srcLocator); 
             }
             XObject argValue = argSequence.item(paramCount);
             XObject argConvertedVal = argValue;
             String paramAsAttrStrVal = ((ElemParam)elem).getAs();              
             if (paramAsAttrStrVal != null) {
                try {
                   argConvertedVal = SequenceTypeSupport.convertXDMValueToAnotherType(argValue, paramAsAttrStrVal, null, xctxt);
                   if (argConvertedVal == null) {
                      throw new TransformerException("XPTY0004 : Function call argument at position " + (paramCount + 1) + " for "
                                                                        + "function {" + funcNameSpaceUri + "}" + funcLocalName + "(), doesn't "
                                                                        + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator); 
                   }
                }
                catch (TransformerException ex) {
                   throw new TransformerException("XPTY0004 : Function call argument at position " + (paramCount + 1) + " for "
                                                                     + "function {" + funcNameSpaceUri + "}" + funcLocalName + "(), doesn't "
                                                                     + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator); 
                }
             }
             
             if (argConvertedVal instanceof ResultSequence) {                
                XNodeSet nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence((ResultSequence)argConvertedVal, 
                                                                                                                            xctxt);
                if (nodeSet != null) {
                   argConvertedVal = nodeSet;  
                }
             }
             
             varStack.setLocalVariable(paramCount, argConvertedVal, nextFrame);
             paramCount++;
          }
          else {
             break; 
          }
      }
      
      varStack.setStackFrame(nextFrame);
      
      int df = transformer.transformToGlobalRTF(this);
      
      NodeList nodeList = (new XRTreeFrag(df, xctxt, this)).convertToNodeset();     
      
      result = new XNodeSetForDOM(nodeList, xctxt);
                        
      XObject funcResultConvertedVal = result;
      
      String funcAsAttrStrVal = getAs();
      
      if (funcAsAttrStrVal != null) {         
         try {
            funcResultConvertedVal = preprocessXslFunctionOrAVariableResult((XNodeSetForDOM)result, funcAsAttrStrVal, xctxt, null);
            
            if (funcResultConvertedVal == null) {
                funcResultConvertedVal = SequenceTypeSupport.convertXDMValueToAnotherType(result, funcAsAttrStrVal, null, xctxt);
                
                if (funcResultConvertedVal == null) {
                   throw new TransformerException("XPTY0004 : The function call result for function {" + funcNameSpaceUri + "}" + funcLocalName + 
                                                                                    "(), doesn't match the declared function result type " + 
                                                                                                                                  funcAsAttrStrVal + ".", srcLocator);   
                }
            }
         }
         catch (TransformerException ex) {
            throw new TransformerException(ex.getMessage(), srcLocator); 
         }
      }
      
      if (funcResultConvertedVal instanceof ResultSequence) {
         ResultSequence resultSeq = (ResultSequence)funcResultConvertedVal;
         int resultSeqSize = resultSeq.size();
         if ((resultSeqSize == 1) && (resultSeq.item(0) instanceof XNodeSet)) {
            funcResultConvertedVal = resultSeq.item(0);   
         }
      }
      
      return funcResultConvertedVal;
  }
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows a xsl:function to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
      super.compose(sroot);
  }
  
  /**
   * This function is called, after xsl:function's children have been composed. 
   * We have to get the count of how many variables have been declared, so we
   * can do a link and unlink.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
      super.endCompose(sroot);
  }
  
  /**
   * This function is called during recomposition to
   * control how this element is composed.
   * 
   * @param root The root stylesheet for this transformation.
   */
  public void recompose(StylesheetRoot root)
  {
      super.recompose(root);
  }
  
  /**
   * This method helps us solve, xsl:function and xsl:variable instruction use cases,
   * when the XSL child contents of xsl:function or xsl:variable instructions contain 
   * xsl:sequence instruction(s).
   * 
   * Given an initial result of computation of, XSL child contents of a xsl:function or xsl:variable
   * instructions, and the function's or variable's expected data type, cast an input data
   * value to the supplied expected data type.
   */
  public static ResultSequence preprocessXslFunctionOrAVariableResult(XNodeSetForDOM xNodeSetForDOM,
                                                                      String sequenceTypeXPathExprStr,
                                                                      XPathContext xctxt, QName varQName) throws TransformerException {
     ResultSequence resultSequence = null;
     
     DTMNodeList dtmNodeList = (DTMNodeList)(xNodeSetForDOM.object());
     
     final int contextNode = xctxt.getContextNode(); 
     SourceLocator srcLocator = xctxt.getSAXLocator(); 
     
     XPath seqTypeXPath = new XPath(sequenceTypeXPathExprStr, srcLocator, 
                                                                       xctxt.getNamespaceContext(), XPath.SELECT, null, true);

     XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());

     SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;

     Node localRootNode = dtmNodeList.item(0);
     NodeList nodeList = localRootNode.getChildNodes();
     int nodeSetLen = nodeList.getLength();
     
     if (nodeSetLen == 1) {
        Node node = nodeList.item(0);
        short nodeType = node.getNodeType();
        if (nodeType == Node.TEXT_NODE) {
             String strVal = ((Text)node).getNodeValue();             
             if (seqExpectedTypeData.getSequenceTypeKindTest() == null) {               
                resultSequence = new ResultSequence();                  
                if (strVal.contains(ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX)) {
                   String[] strParts = strVal.split(ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX);
                   for (int idx = 0; idx < strParts.length; idx++) {
                      String seqItemStrVal = strParts[idx];
                      XObject xObject = getXSTypedAtomicValue(seqItemStrVal, seqExpectedTypeData.getSequenceType());
                      if (xObject != null) {
                         resultSequence.add(xObject);
                      }
                   }
                }
                else {
                   XObject xObject = getXSTypedAtomicValue(strVal, seqExpectedTypeData.getSequenceType());
                   if (xObject != null) {
                      resultSequence.add(xObject);
                   }
                }
             }
         }
     }
     
     if (seqExpectedTypeData.getSequenceTypeKindTest() == null) {
         if ((seqExpectedTypeData.getItemTypeOccurrenceIndicator() == 0) || 
                                                               (seqExpectedTypeData.getItemTypeOccurrenceIndicator() == 
                                                                                                               SequenceTypeSupport.OccurenceIndicator.ZERO_OR_ONE)) {
             if ((resultSequence != null) && (resultSequence.size() > 1)) {
                String errMesg = null;
                if (m_name != null) {
                   errMesg = "XTTE0780 : A sequence of more than one item, is not allowed as a result of call to function '" + m_name.toString() + "'. "
                                                                                + "The expected result type of this function is " + sequenceTypeXPathExprStr + "."; 
                }
                else {
                   errMesg = "XTTE0570 : A sequence of more than one item, is not allowed as the value of variable '$" + varQName.toString() + "'. "
                                                                                + "This variable has expected type " + sequenceTypeXPathExprStr + "."; 
                }
                
                throw new TransformerException(errMesg); 
             }
         }
     }
     
     return resultSequence;
  }
  
  /**
   * Given XalanJ's integer code value of, an XML Schema built-in data type and a 
   * string representation of a data value, construct XalanJ's typed data object 
   * corresponding to the data type's integer code value. 
   */
  private static XObject getXSTypedAtomicValue(String strVal, int sequenceType) throws TransformerException {
      
      XObject result = null;
     
      if (sequenceType == SequenceTypeSupport.BOOLEAN) {
         boolean boolVal = ("false".equals(strVal) || "0".equals(strVal)) ? false : true;
         result = new XSBoolean(boolVal);
      }
      else if (sequenceType == SequenceTypeSupport.STRING) {
         result = new XSString(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_DATE) {
         result = XSDate.parseDate(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_DATETIME) {
         result = XSDateTime.parseDateTime(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_TIME) {
         // TO DO
      }
      else if (sequenceType == SequenceTypeSupport.XS_DURATION) {
         result = XSDuration.parseDuration(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_DAYTIME_DURATION) {
         result = XSDayTimeDuration.parseDayTimeDuration(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_YEARMONTH_DURATION) {
         result = XSYearMonthDuration.parseYearMonthDuration(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_DECIMAL) {
         result = new XSDecimal(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_INTEGER) {
         result = new XSInteger(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_LONG) {
         result = new XSLong(strVal);
      }
      else if (sequenceType == SequenceTypeSupport.XS_INT) {
         result = new XSInt(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_DOUBLE) {
         result = new XSDouble(strVal); 
      }
      else if (sequenceType == SequenceTypeSupport.XS_FLOAT) {
         result = new XSFloat(strVal); 
      }
     
      return result;
  }

}
