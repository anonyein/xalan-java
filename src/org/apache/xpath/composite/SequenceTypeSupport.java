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
package org.apache.xpath.composite;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
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
import org.apache.xpath.xs.types.XSNumericType;
import org.apache.xpath.xs.types.XSString;
import org.apache.xpath.xs.types.XSTime;
import org.apache.xpath.xs.types.XSUntyped;
import org.apache.xpath.xs.types.XSUntypedAtomic;
import org.apache.xpath.xs.types.XSYearMonthDuration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides few utility methods, to help evaluate
 * XPath 3.1 sequence type expressions.
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#id-sequencetype-syntax
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class SequenceTypeSupport {
    
    /** 
     * Following are various constant int values, denoting XPath 3.1 and 
     * XML Schema data types.
     */
        
    public static int EMPTY_SEQUENCE = 1;
    
    // Represents both XML Schema data type xs:boolean, and 
    // XalanJ legacy boolean data type.
    public static int BOOLEAN = 2;
    
    // Represents both XML Schema data type xs:string, and 
    // XalanJ legacy string data type.
    public static int STRING = 3;
    
    public static int XS_DATE = 4;
    
    public static int XS_DATETIME = 5;
    
    public static int XS_TIME = 6;
    
    public static int XS_DURATION = 7;
    
    public static int XS_DAYTIME_DURATION = 8;
    
    public static int XS_YEARMONTH_DURATION = 9;
    
    public static int XS_DECIMAL = 10;
    
    public static int XS_INTEGER = 11;
    
    public static int XS_LONG = 12;
    
    public static int XS_INT = 13;
    
    public static int XS_DOUBLE = 14;
    
    public static int XS_FLOAT = 15;
    
    public static int XS_UNTYPED_ATOMIC = 16;
    
    public static int XS_UNTYPED = 17;
    
    /** 
     * Following are constant int values denoting XPath 3.1 sequence
     * type KindTest expressions.
     */
    
    public static int ELEMENT_KIND = 101;
    
    public static int ATTRIBUTE_KIND = 102;
    
    public static int TEXT_KIND = 103;
    
    public static int NAMESPACE_NODE_KIND = 104;
    
    public static int NODE_KIND = 105;
    
    public static int ITEM_KIND = 106;
    
        
    public static class OccurenceIndicator {
       // Represents the sequence type occurrence indicator '?'
       public static int ZERO_OR_ONE = 1;
       
       // Represents the sequence type occurrence indicator '*'
       public static int ZERO_OR_MANY = 2;
       
       // Represents the sequence type occurrence indicator '+'
       public static int ONE_OR_MANY = 3;
    }
    
    public static String Q_MARK = "?";
    
    public static String STAR = "*";
    
    public static String PLUS = "+";
    
    
    /**
     * This method converts/casts an XPath 3.1 xdm source value represented by an
     * XObject object instance, to a value of another xdm data type.
     * 
     * For XPath sequence type expressions that represent KindTest (i.e,
     * element(), attribute() etc), this method only checks whether an XML input
     * item conforms with the provided KindTest sequence type expression, and
     * returns an input value unchanged.  
     * 
     * This method is called recursively at certain places.
     *  
     * @param srcValue                     an XObject object instance that represents a
     *                                     source xdm value. 
     * @param sequenceTypeXPathExprStr     an XPath sequence type expression string 
     * @param seqExpectedTypeDataInp       an XPath sequence type compiled expression                                                                  
     * @param xctxt                        the current XPath context object
     * 
     * @return                             an XObject object instance produced, as a result of data type 
     *                                     conversion performed by this method on an object instance
     *                                     srcValue.
     *                                
     * @throws TransformerException 
     */
    public static XObject convertXDMValueToAnotherType(XObject srcValue, String sequenceTypeXPathExprStr, 
                                                                                       SequenceTypeData seqExpectedTypeDataInp, 
                                                                                       XPathContext xctxt) throws TransformerException {
        XObject result = null;
        
        int contextNode = DTM.NULL;        
        SourceLocator srcLocator = null;
        
        if (xctxt != null) {
           contextNode = xctxt.getContextNode();        
           srcLocator = xctxt.getSAXLocator();
        }
        
        try {
            XPath seqTypeXPath = null;
            XObject seqTypeExpressionEvalResult = null;
            SequenceTypeData seqExpectedTypeData = null;
            
            if (xctxt != null) {
               seqTypeXPath = new XPath(sequenceTypeXPathExprStr, srcLocator, 
                                                                          xctxt.getNamespaceContext(), XPath.SELECT, null, true);
            
               seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
            
               seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
            }
            else {
               seqExpectedTypeData = seqExpectedTypeDataInp; 
            }
            
            int expectedType = seqExpectedTypeData.getSequenceType();            
            int itemTypeOccurenceIndicator = seqExpectedTypeData.getItemTypeOccurrenceIndicator();
            SequenceTypeKindTest sequenceTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
            
            if (srcValue instanceof XString) {
                String srcStrVal = ((XString)srcValue).str();
                
                if (expectedType == STRING) {
                   result = srcValue; 
                }
                else if (sequenceTypeKindTest != null) {
                   if (sequenceTypeKindTest.getKindVal() == TEXT_KIND) {
                      result = srcValue; 
                   }
                   else {
                      result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:string", sequenceTypeXPathExprStr);
                   }
                }
                else {
                   result = castStringValueToAnExpectedType(srcStrVal, expectedType);
                }
            }
            else if (srcValue instanceof XSString) {           
               String srcStrVal = ((XSString)srcValue).stringValue();
               
               if (expectedType == STRING) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  if (sequenceTypeKindTest.getKindVal() == TEXT_KIND) {
                     result = srcValue; 
                  }
                  else {
                     result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                    "xs:string", sequenceTypeXPathExprStr);
                  }
               }
               else {
                  result = castStringValueToAnExpectedType(srcStrVal, expectedType);
               }
            }
            else if (srcValue instanceof XNumber) {
               XSDouble xsDouble = new XSDouble(((XNumber)srcValue).num());
               String srcStrVal = xsDouble.stringValue(); 
               
               if (expectedType == XS_DOUBLE) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                   "xs:double", sequenceTypeXPathExprStr);
               }
               else {
                  result = performXDMNumericTypeConversion(xsDouble, expectedType);
               }
            }
            else if (srcValue instanceof XSNumericType) {
               XSNumericType xsNumericType = (XSNumericType)srcValue;
               
               try {
                   result = performXDMNumericTypeConversion(xsNumericType, expectedType);
               }
               catch (TransformerException ex1) {
                  if (sequenceTypeKindTest != null) {
                     String srcStrVal = xsNumericType.stringValue();
                     try {
                        result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                         xsNumericType.stringType(), sequenceTypeXPathExprStr);
                     }
                     catch (TransformerException ex2) {
                        throw ex2; 
                     }
                  }
                  else {
                     throw ex1;   
                  }
               }
            }
            else if (srcValue instanceof XBoolean) {
               String srcStrVal = ((XBoolean)srcValue).str();
               if (expectedType == BOOLEAN) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                    "xs:boolean", sequenceTypeXPathExprStr);
               }
            }
            else if (srcValue instanceof XSBoolean) {
               String srcStrVal = ((XSBoolean)srcValue).stringValue();
               if (expectedType == BOOLEAN) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                    "xs:boolean", sequenceTypeXPathExprStr);
               } 
            }
            else if (srcValue instanceof XSDate) {
               String srcStrVal = ((XSDate)srcValue).stringValue();
               if (expectedType == XS_DATE) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                    "xs:date", sequenceTypeXPathExprStr);
               } 
            }
            else if (srcValue instanceof XSDateTime) {
               String srcStrVal = ((XSDateTime)srcValue).stringValue();
               if (expectedType == XS_DATETIME) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                    "xs:dateTime", sequenceTypeXPathExprStr);
               }
            }
            else if (srcValue instanceof XSTime) {
               String srcStrVal = ((XSTime)srcValue).stringValue();
               if (expectedType == XS_TIME) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                   "xs:time", sequenceTypeXPathExprStr);
               }
            }
            else if (srcValue instanceof XSDuration) {
               String srcStrVal = ((XSDuration)srcValue).stringValue();
               if (expectedType == XS_DURATION) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                   "xs:duration", sequenceTypeXPathExprStr);
               } 
            }
            else if (srcValue instanceof XSDayTimeDuration) {
               String srcStrVal = ((XSDayTimeDuration)srcValue).stringValue();     
               if (expectedType == XS_DAYTIME_DURATION) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                   "xs:dayTimeDuration", sequenceTypeXPathExprStr);
               } 
            }
            else if (srcValue instanceof XSYearMonthDuration) {
               String srcStrVal = ((XSYearMonthDuration)srcValue).stringValue();      
               if (expectedType == XS_YEARMONTH_DURATION) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                   "xs:yearMonthDuration", sequenceTypeXPathExprStr);
               } 
            }
            else if (srcValue instanceof XSUntyped) {
               String srcStrVal = ((XSUntyped)srcValue).stringValue();
               result = convertXDMValueToAnotherType(new XSString(srcStrVal), sequenceTypeXPathExprStr, 
                                                                                                    seqExpectedTypeDataInp, xctxt);
            }
            else if (srcValue instanceof XSUntypedAtomic) {
               String srcStrVal = ((XSUntypedAtomic)srcValue).stringValue();
               result = convertXDMValueToAnotherType(new XSString(srcStrVal), sequenceTypeXPathExprStr, 
                                                                                                    seqExpectedTypeDataInp, xctxt);
            }
            else if (srcValue instanceof XNodeSetForDOM) {               
               result = castXNodeSetForDOMInstance(srcValue, sequenceTypeXPathExprStr, seqExpectedTypeDataInp, 
                                                                              xctxt, srcLocator, itemTypeOccurenceIndicator, 
                                                                                                                       sequenceTypeKindTest);
            }
            else if (srcValue instanceof XNodeSet) {
               result = castXNodeSetInstance(srcValue, sequenceTypeXPathExprStr, seqExpectedTypeDataInp, 
                                                                              xctxt, srcLocator, itemTypeOccurenceIndicator, 
                                                                                                                       sequenceTypeKindTest);
            }
            else if (srcValue instanceof ResultSequence) {
               ResultSequence srcResultSeq = (ResultSequence)srcValue;
               if (srcResultSeq.size() == 1) {
                   result = convertXDMValueToAnotherType(srcResultSeq.item(0), sequenceTypeXPathExprStr, seqExpectedTypeDataInp, xctxt);   
               }
               else {
                   result = castResultSequenceInstance(srcValue, sequenceTypeXPathExprStr, seqExpectedTypeDataInp, xctxt, srcLocator, 
                                                                                                               expectedType, itemTypeOccurenceIndicator);
               }
            }
        }
        catch (TransformerException ex) {
           if (ex.getLocator() != null) {
              throw ex;  
           }
           else {
              throw new TransformerException(ex.getMessage(), srcLocator);  
           }
        }
        catch (Exception ex) {
            String srcStrVal = XslTransformEvaluationHelper.getStrVal(srcValue); 
            throw new TransformerException("XTTE0570 : The source value '" + srcStrVal + "' cannot be cast "
                                                                                    + "to a type " + sequenceTypeXPathExprStr + ".", srcLocator); 
        }
        
        return result;
    }
    
    /**
     * Check whether, two XML namespace uris are equal.
     */
    public static boolean isTwoXmlNamespacesEqual(String nsUr1, String nsUri2) {
        boolean xmlNamespacesEqual = false;
        
        if ((nsUr1 == null) && (nsUri2 == null)) {
           xmlNamespacesEqual = true; 
        }
        else if ((nsUr1 != null) && (nsUri2 != null)) {
           xmlNamespacesEqual = nsUr1.equals(nsUri2);  
        }
        
        return xmlNamespacesEqual; 
    }
    
    /**
     * Cast a string value to an expected xdm type.
     * 
     * @throws TransformerException
     */
    private static XObject castStringValueToAnExpectedType(String srcStrVal, int expectedType) throws TransformerException {
        
        XObject result = null;
        
        try {
            if (expectedType == BOOLEAN) {
               if ("0".equals(srcStrVal) || "false".equals(srcStrVal)) {
                  result = new XSBoolean(false); 
               }
               else if ("1".equals(srcStrVal) || "true".equals(srcStrVal)) {
                  result = new XSBoolean(true); 
               }
            }
            else if (expectedType == XS_DECIMAL) {
               result = new XSDecimal(srcStrVal);  
            }
            else if (expectedType == XS_INTEGER) {
               result = new XSInteger(srcStrVal); 
            }
            else if (expectedType == XS_LONG) {
               result = new XSLong(srcStrVal); 
            }
            else if (expectedType == XS_INT) {
               result = new XSInt(srcStrVal);
            }
            else if (expectedType == XS_DOUBLE) {
               result = new XSDouble(srcStrVal); 
            }
            else if (expectedType == XS_FLOAT) {
               result = new XSFloat(srcStrVal); 
            }
            else if (expectedType == XS_DATE) {
               result = XSDate.parseDate(srcStrVal); 
            }
            else if (expectedType == XS_DATETIME) {
               result = XSDateTime.parseDateTime(srcStrVal); 
            }
            else if (expectedType == XS_DURATION) {
               result = XSDuration.parseDuration(srcStrVal); 
            }
            else if (expectedType == XS_DAYTIME_DURATION) {
               result = XSDayTimeDuration.parseDayTimeDuration(srcStrVal); 
            }
            else if (expectedType == XS_YEARMONTH_DURATION) {
               result = XSYearMonthDuration.parseYearMonthDuration(srcStrVal); 
            }
            else {
               throw new TransformerException("XTTE0570 : The string value '" + srcStrVal + "' cannot be "
                                                                                                  + "cast to a type " + getDataTypeNameFromIntValue(expectedType) + "."); 
            }
        }
        catch (TransformerException ex) {
            throw ex;    
        }
        catch (Exception ex) {
            throw new TransformerException("XTTE0570 : The string value '" + srcStrVal + "' cannot be cast to "
                                                                                               + "a type " + getDataTypeNameFromIntValue(expectedType) + ".");
        }
        
        return result;
    }
    
    /**
     * This method performs XPath numeric type conversions, and numeric type
     * promotion as defined by XPath 3.1 spec (ref, https://www.w3.org/TR/xpath-31/#promotion).
     */
    private static XObject performXDMNumericTypeConversion(XSNumericType srcXsNumericType, 
                                                                             int expectedType) throws TransformerException {
        XObject result = null;
        
        String srcStrVal = srcXsNumericType.stringValue();
        
        try {
            if (srcXsNumericType instanceof XSFloat) {           
               if (expectedType == XS_FLOAT) {
                  // The source and expected data types are same. Return the original value unchanged.
                  result = srcXsNumericType; 
               }
               else if (expectedType == XS_DOUBLE) {
                  result = new XSDouble(srcStrVal);
               }
            }
            else if (expectedType == XS_INT) {
                result = new XSInt(srcStrVal);
            }
            else if (expectedType == XS_LONG) {
                result = new XSLong(srcStrVal); 
            }
            else if (expectedType == XS_INTEGER) {
                result = new XSInteger(srcStrVal); 
            }
            else if (srcXsNumericType instanceof XSDecimal) {           
               if (expectedType == XS_DECIMAL) {
                  // The source and expected data types are same. Return the original value unchanged.
                  result = srcXsNumericType; 
               }
               else if (expectedType == XS_FLOAT) {
                  result = new XSFloat(srcStrVal); 
               }
               else if (expectedType == XS_DOUBLE) {
                  result = new XSDouble(srcStrVal); 
               }
            }
            else if (expectedType == XS_DECIMAL) {
               result = new XSDecimal(srcStrVal);  
            }
            else if (expectedType == XS_DOUBLE) {
               result = new XSDouble(srcStrVal); 
            }
            else if (expectedType == XS_FLOAT) {
               result = new XSFloat(srcStrVal); 
            }
            else if ((srcXsNumericType.stringType()).equals(getDataTypeNameFromIntValue(expectedType))) {
               // The source and expected data types are same. Return the original value unchanged.
               result = srcXsNumericType;
            }
            else {
               throw new TransformerException("XTTE0570 : The numeric value " + srcStrVal + " cannot be cast "
                                                                                                 + "to a type " + getDataTypeNameFromIntValue(expectedType) + ".");  
            }
        }
        catch (TransformerException ex) {
            throw ex;  
        }
        catch (Exception ex) {
            throw new TransformerException("XTTE0570 : The numeric value " + srcStrVal + " cannot be cast "
                                                                                               + "to a type " + getDataTypeNameFromIntValue(expectedType) + ".");
        }
        
        return result;
    }
    
    /**
     * Given a primitive int value for an XDM data type, return the corresponding
     * data type's name.
     */
    private static String getDataTypeNameFromIntValue(int sequenceType) {
       
       String dataTypeName = null;
       
       if (sequenceType == EMPTY_SEQUENCE) {
          dataTypeName = "empty-sequence()";  
       }
       else if (sequenceType == BOOLEAN) {
          dataTypeName = "xs:boolean"; 
       }
       else if (sequenceType == STRING) {
          dataTypeName = "xs:string"; 
       }
       else if (sequenceType == XS_DATE) {
          dataTypeName = "xs:date"; 
       }
       else if (sequenceType == XS_DATETIME) {
          dataTypeName = "xs:dateTime";
       }
       else if (sequenceType == XS_TIME) {
          dataTypeName = "xs:time"; 
       }
       else if (sequenceType == XS_DURATION) {
          dataTypeName = "xs:duration"; 
       }
       else if (sequenceType == XS_DAYTIME_DURATION) {
          dataTypeName = "xs:dayTimeDuration"; 
       }
       else if (sequenceType == XS_YEARMONTH_DURATION) {
          dataTypeName = "xs:yearMonthDuration"; 
       }
       else if (sequenceType == XS_DECIMAL) {
          dataTypeName = "xs:decimal";
       }
       else if (sequenceType == XS_INTEGER) {
          dataTypeName = "xs:integer";
       }
       else if (sequenceType == XS_LONG) {
          dataTypeName = "xs:long"; 
       }
       else if (sequenceType == XS_INT) {
          dataTypeName = "xs:int"; 
       }
       else if (sequenceType == XS_DOUBLE) {
          dataTypeName = "xs:double"; 
       }
       else if (sequenceType == XS_FLOAT) {
          dataTypeName = "xs:float"; 
       }
       
       return dataTypeName;       
    }
    
    /**
     * Given an XObject object instance representing an atomic data value, check whether
     * a sequence type item() type annotation could be applied.
     */
    private static XObject performXdmItemTypeNormalizationOnAtomicType(SequenceTypeKindTest sequenceTypeKindTest, 
                                                                                     XObject srcValue, String srcStrVal, 
                                                                                     String srcDataTypeName, 
                                                                                     String sequenceTypeXPathExprStr) throws TransformerException {
        XObject result = null;
        
        if (sequenceTypeKindTest.getKindVal() == ITEM_KIND) {
           result = srcValue;
        }
        else {
           throw new TransformerException("XTTE0570 : The " + srcDataTypeName + " value '" + srcStrVal + "' cannot be cast to "
                                                                                                   + "a type " + sequenceTypeXPathExprStr + "."); 
        }
        
        return result;
    }
    
    /**
     * This method helps to support following XSL transformation actions,
     * 
     * 1) An xsl:variable element has an "as" attribute (specifying the expected type of variable's value),
     *    not having a "select" attribute, and having a contained sequence constructor (which when
     *    evaluated, construct's the variable's value). An xsl:variable's evaluated value passed as an
     *    argument to this method, is checked against the variable's expected type.
     *      
     * 2) An xsl:template element has an "as" attribute (specifying the expected type of template's
     *    evaluated content). The template's evaluated content passed as an argument to this method, 
     *    is checked against the expected type.  
     */
    private static XObject castXNodeSetForDOMInstance(XObject srcValue,
                                                                  String sequenceTypeXPathExprStr,
                                                                  SequenceTypeData seqExpectedTypeDataInp, XPathContext xctxt,
                                                                  SourceLocator srcLocator, int itemTypeOccurenceIndicator,
                                                                  SequenceTypeKindTest sequenceTypeKindTest)
                                                                                                          throws TransformerException {
        XObject result = null;

        XNodeSetForDOM xNodeSetForDOM = (XNodeSetForDOM)srcValue;
        DTMNodeList dtmNodeList = (DTMNodeList)(xNodeSetForDOM.object());

        DTMManager dtmMgr = xNodeSetForDOM.getDTMManager();

        List<Integer> xdmNodesDtmList = new ArrayList<Integer>();

        Node localRootNode = dtmNodeList.item(0);
        NodeList nodeList = localRootNode.getChildNodes();
        int nodeSetLen = nodeList.getLength();

        ResultSequence convertedResultSeq = new ResultSequence();
        
        if ((nodeSetLen > 1) && ((itemTypeOccurenceIndicator == 0) || (itemTypeOccurenceIndicator == 
                                                                                             OccurenceIndicator.ZERO_OR_ONE))) {
            throw new TransformerException("XTTE0570 : A sequence of size " + nodeSetLen + ", cannot be cast to a type " 
                                                                                                         + sequenceTypeXPathExprStr + ".", srcLocator);  
        }
        else {            
            for (int idx = 0; idx < nodeSetLen; idx++) {
                Node node = nodeList.item(idx);

                int nodeDtmHandle = dtmMgr.getDTMHandleFromNode(node);
                xdmNodesDtmList.add(Integer.valueOf(nodeDtmHandle));

                String sequenceTypeNewXPathExprStr = null;                    
                if (sequenceTypeXPathExprStr.endsWith(Q_MARK) || sequenceTypeXPathExprStr.endsWith(STAR) || 
                                                                                               sequenceTypeXPathExprStr.endsWith(PLUS)) {
                    sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr.substring(0, sequenceTypeXPathExprStr.length() - 1);  
                }

                if (sequenceTypeKindTest != null) {
                    String nodeName = node.getLocalName();
                    String nodeNsUri = node.getNamespaceURI();

                    if (sequenceTypeKindTest.getDataTypeName() != null) {
                        String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr; 
                        throw new TransformerException("XTTE0570 : The required item type of an XML instance node is " + dataTypeStr + 
                                                                                                        ". The supplied value " + nodeName + " does not match. The "
                                                                                                        + "supplied node has not been validated with a schema.", srcLocator); 
                    }
                    else {
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            String elemNodeKindTestNodeName = sequenceTypeKindTest.getNodeLocalName();
                            if (elemNodeKindTestNodeName == null || "".equals(elemNodeKindTestNodeName) || 
                                                                                                   STAR.equals(elemNodeKindTestNodeName)) {
                                elemNodeKindTestNodeName = nodeName;  
                            }

                            boolean isSeqTypeMatchOk = false;

                            if ((sequenceTypeKindTest.getKindVal() == ELEMENT_KIND) && (nodeName.equals(elemNodeKindTestNodeName)) 
                                                                                             && (isTwoXmlNamespacesEqual(nodeNsUri, sequenceTypeKindTest.getNodeNsUri()))) {
                                isSeqTypeMatchOk = true;
                            }
                            else if ((sequenceTypeKindTest.getKindVal() == NODE_KIND) || (sequenceTypeKindTest.getKindVal() == ITEM_KIND)) {
                                isSeqTypeMatchOk = true;
                            }

                            if (!isSeqTypeMatchOk) {
                                String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                                throw new TransformerException("XTTE0570 : The required item type of an XML instance node is " + dataTypeStr + 
                                                                                                                ". The supplied value " + nodeName + " does not match.", srcLocator); 
                            }
                        }
                        else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                            String attrNodeKindTestNodeName = sequenceTypeKindTest.getNodeLocalName();
                            if (attrNodeKindTestNodeName == null || "".equals(attrNodeKindTestNodeName) || 
                                                                                                   STAR.equals(attrNodeKindTestNodeName)) {
                                attrNodeKindTestNodeName = nodeName;  
                            }

                            boolean isSeqTypeMatchOk = false;

                            if ((sequenceTypeKindTest.getKindVal() == ATTRIBUTE_KIND) && (nodeName.equals(attrNodeKindTestNodeName)) 
                                                                                               && (isTwoXmlNamespacesEqual(nodeNsUri, sequenceTypeKindTest.getNodeNsUri()))) {
                                isSeqTypeMatchOk = true;  
                            }
                            else if ((sequenceTypeKindTest.getKindVal() == NODE_KIND) || (sequenceTypeKindTest.getKindVal() == ITEM_KIND)) {
                                isSeqTypeMatchOk = true;  
                            }

                            if (!isSeqTypeMatchOk) {
                                String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                                throw new TransformerException("XTTE0570 : The required item type of an XML instance node is " + dataTypeStr + 
                                                                                                      ". The supplied value " + nodeName + " does not match.", srcLocator);  
                            } 
                        }
                        else if (node.getNodeType() == Node.TEXT_NODE) {
                            if (!((sequenceTypeKindTest.getKindVal() == TEXT_KIND) || 
                                                                (sequenceTypeKindTest.getKindVal() == NODE_KIND) || 
                                                                (sequenceTypeKindTest.getKindVal() == ITEM_KIND))) {
                                String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                                throw new TransformerException("XTTE0570 : The required item type of an XML instance node is " + dataTypeStr + ". "
                                                                                                      + "The supplied value does not match.", srcLocator);  
                            }
                        }
                    }
                }
                else {
                    if (sequenceTypeNewXPathExprStr == null) {
                        sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr;  
                    }

                    String nodeStrVal = node.getTextContent();
                    XObject xObject = convertXDMValueToAnotherType(new XSString(nodeStrVal), sequenceTypeNewXPathExprStr, 
                                                                                                                      seqExpectedTypeDataInp, xctxt);                       
                    convertedResultSeq.add(xObject); 
                }
            }
        }

        if (convertedResultSeq.size() > 0) {
            result = convertedResultSeq;  
        }
        else if (xdmNodesDtmList.size() > 0) {
            result = new XNodeSet(xdmNodesDtmList, dtmMgr); 
        }
        
        return result;
    }
    
    /**
     * This method casts a provided XObject object instance representing an xdm
     * node set, to another type specified by an xdm sequence type expression.
     */
    private static XObject castXNodeSetInstance(XObject srcValue,
                                                              String sequenceTypeXPathExprStr,
                                                              SequenceTypeData seqExpectedTypeDataInp, XPathContext xctxt,
                                                              SourceLocator srcLocator,
                                                              int itemTypeOccurenceIndicator,
                                                              SequenceTypeKindTest sequenceTypeKindTest)
                                                                                                     throws TransformerException {
        XObject result = null;
        
        XNodeSet xdmNodeSet = (XNodeSet)srcValue;
        
        int nodeSetLen = xdmNodeSet.getLength();
        
        if ((nodeSetLen > 1) && ((itemTypeOccurenceIndicator == 0) || (itemTypeOccurenceIndicator == 
                                                                                             OccurenceIndicator.ZERO_OR_ONE))) {
            throw new TransformerException("XTTE0570 : A sequence of size " + nodeSetLen + ", cannot be cast to a type " 
                                                                                                           + sequenceTypeXPathExprStr + ".", srcLocator);  
        }
        else { 
            ResultSequence convertedResultSeq = new ResultSequence();
            
            DTMIterator dtmIter = xdmNodeSet.iterRaw();
            
            int nextNodeDtmHandle;
                   
            while ((nextNodeDtmHandle = dtmIter.nextNode()) != DTM.NULL) {               
               XNodeSet nodeSetItem = new XNodeSet(nextNodeDtmHandle, xctxt);
               
               String sequenceTypeNewXPathExprStr = null;
               if (sequenceTypeXPathExprStr != null) {
                   if (sequenceTypeXPathExprStr.endsWith(Q_MARK) || sequenceTypeXPathExprStr.endsWith(STAR) || 
                                                                                                sequenceTypeXPathExprStr.endsWith(PLUS)) {
                      sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr.substring(0, sequenceTypeXPathExprStr.length() - 1);  
                   }
               }
               
               if (sequenceTypeKindTest != null) {
                  DTM dtm = dtmIter.getDTM(nextNodeDtmHandle);
                  
                  String nodeName = dtm.getNodeName(nextNodeDtmHandle);
                  String nodeNsUri = dtm.getNamespaceURI(nextNodeDtmHandle);
                  
                  if (sequenceTypeKindTest.getDataTypeName() != null) {
                     String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr; 
                     throw new TransformerException("XTTE0570 : The required item type of an XML instance node is " + dataTypeStr + 
                                                                                                     ". The supplied value " + nodeName + " does not match. The "
                                                                                                     + "supplied node has not been validated with a schema.", srcLocator); 
                  }
                  else {
                     if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                        String elemNodeKindTestNodeName = sequenceTypeKindTest.getNodeLocalName();
                        if (elemNodeKindTestNodeName == null || "".equals(elemNodeKindTestNodeName) || 
                                                                                                STAR.equals(elemNodeKindTestNodeName)) {
                           elemNodeKindTestNodeName = nodeName;  
                        }
                        
                        if ((sequenceTypeKindTest.getKindVal() == ELEMENT_KIND) && (nodeName.equals(elemNodeKindTestNodeName)) 
                                                                                                 && (isTwoXmlNamespacesEqual(nodeNsUri, sequenceTypeKindTest.getNodeNsUri()))) {
                           convertedResultSeq.add(nodeSetItem);  
                        }
                        else if ((sequenceTypeKindTest.getKindVal() == NODE_KIND) || (sequenceTypeKindTest.getKindVal() == ITEM_KIND)) {
                           convertedResultSeq.add(nodeSetItem); 
                        }
                        else {
                           String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                           throw new TransformerException("XTTE0570 : The required item type of an XML instance node is " + dataTypeStr + 
                                                                                                 ". The supplied value " + nodeName + " does not match.", srcLocator); 
                        }
                     }
                     else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                        String attrNodeKindTestNodeName = sequenceTypeKindTest.getNodeLocalName();
                        if (attrNodeKindTestNodeName == null || "".equals(attrNodeKindTestNodeName) || 
                                                                                                STAR.equals(attrNodeKindTestNodeName)) {
                           attrNodeKindTestNodeName = nodeName;  
                        }
                        
                        if ((sequenceTypeKindTest.getKindVal() == ATTRIBUTE_KIND) && (nodeName.equals(attrNodeKindTestNodeName)) 
                                                                                                   && (isTwoXmlNamespacesEqual(nodeNsUri, sequenceTypeKindTest.getNodeNsUri()))) {
                            convertedResultSeq.add(nodeSetItem);  
                        }
                        else if ((sequenceTypeKindTest.getKindVal() == NODE_KIND) || (sequenceTypeKindTest.getKindVal() == ITEM_KIND)) {
                            convertedResultSeq.add(nodeSetItem); 
                        }
                        else {
                            String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                            throw new TransformerException("XTTE0570 : The required item type of an XML instance node is " + dataTypeStr + 
                                                                                                  ". The supplied value " + nodeName + " does not match.", srcLocator);  
                        }
                     }
                     else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.TEXT_NODE) {
                        if (sequenceTypeKindTest.getKindVal() == TEXT_KIND) {
                           convertedResultSeq.add(nodeSetItem); 
                        }
                        else {
                           String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                           throw new TransformerException("XTTE0570 : The required item type of an XML instance node is " + dataTypeStr + ". "
                                                                                                            + "The supplied value does not match.", srcLocator); 
                        }
                     }
                     else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.NAMESPACE_NODE) {
                         if (sequenceTypeKindTest.getKindVal() == NAMESPACE_NODE_KIND) {
                            convertedResultSeq.add(nodeSetItem); 
                         }
                         else {
                            String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                            throw new TransformerException("XTTE0570 : The required item type of an XML instance node is " + dataTypeStr + ". "
                                                                                                             + "The supplied value does not match.", srcLocator); 
                         }
                     }
                     else {
                        convertedResultSeq.add(nodeSetItem); 
                     }
                  }
               }
               else {
                  if (sequenceTypeNewXPathExprStr == null) {
                     sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr;  
                  }
                  
                  if (!((xctxt == null) && ((seqExpectedTypeDataInp != null) && 
                                                               (seqExpectedTypeDataInp.getSequenceTypeKindTest() == null)))) {
                     String nodeStrVal = nodeSetItem.str();
                     XObject xObject = convertXDMValueToAnotherType(new XSString(nodeStrVal), sequenceTypeNewXPathExprStr, 
                                                                                                                    seqExpectedTypeDataInp, xctxt);                       
                     convertedResultSeq.add(xObject);
                  }
                  else {
                     throw new TransformerException("XTTE0570 : The source xdm sequence cannot be cast "
                                                                                         + "to the provided sequence type.", srcLocator); 
                  }
               }
            }
            
            result = convertedResultSeq;
        }
        
        return result;
    }
    
    /**
     * This method casts a provided XDM evaluated sequence, to another type 
     * specified by an xdm sequence type expression.
     */
    private static XObject castResultSequenceInstance(XObject srcValue,
                                                                    String sequenceTypeXPathExprStr,
                                                                    SequenceTypeData seqExpectedTypeDataInp, XPathContext xctxt,
                                                                    SourceLocator srcLocator, int expectedType,
                                                                    int itemTypeOccurenceIndicator) throws TransformerException {
        
        XObject result = null;
        
        ResultSequence srcResultSeq = (ResultSequence)srcValue;
        
        int seqLen = srcResultSeq.size();
        
        if ((seqLen == 0) && (itemTypeOccurenceIndicator == OccurenceIndicator.ONE_OR_MANY)) {
           throw new TransformerException("XTTE0570 : An empty sequence is not allowed, as result of evaluation for sequence "
                                                                                                              + "type's occurence indicator +.", srcLocator);  
        }
        else if ((seqLen > 0) && (expectedType == EMPTY_SEQUENCE)) {
           throw new TransformerException("XTTE0570 : The sequence doesn't conform to an expected type empty-sequence(). "
                                                                                               + "The supplied sequence has size " + seqLen + ".", srcLocator);  
        }
        else if ((seqLen > 1) && (itemTypeOccurenceIndicator == OccurenceIndicator.ZERO_OR_ONE)) {
            throw new TransformerException("XTTE0570 : A sequence of size " + seqLen + ", cannot be cast to a type " 
                                                                                                                + sequenceTypeXPathExprStr + ".", srcLocator); 
        }
        else {
            String sequenceTypeNewXPathExprStr = null;
            if (sequenceTypeXPathExprStr != null) {
                if (sequenceTypeXPathExprStr.endsWith(Q_MARK) || sequenceTypeXPathExprStr.endsWith(STAR) || 
                                                                                                sequenceTypeXPathExprStr.endsWith(PLUS)) {
                   sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr.substring(0, sequenceTypeXPathExprStr.length() - 1);  
                }
            }
            
            ResultSequence convertedResultSeq = new ResultSequence();
            
            for (int idx = 0; idx < srcResultSeq.size(); idx++) {
               XObject seqItem = (XObject)(srcResultSeq.item(idx));                       
               XObject convertedSeqItem = convertXDMValueToAnotherType(seqItem, sequenceTypeNewXPathExprStr, 
                                                                                                         seqExpectedTypeDataInp, xctxt);
               convertedResultSeq.add(convertedSeqItem);
            }
            
            result = convertedResultSeq; 
        }
        
        return result;
    }

}
