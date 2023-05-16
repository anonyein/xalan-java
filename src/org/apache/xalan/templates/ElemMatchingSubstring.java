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
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;

/**
   XSLT 3.0 matching-substring element.
   
   <xsl:matching-substring>
      <!-- Content: sequence-constructor -->
   </xsl:matching-substring>
   
   @author Mukul Gandhi <mukulg@apache.org>
  
 * @xsl.usage advanced
 */

/*
 * Implementation of the XSLT 3.0 xsl:matching-substring instruction.
 * 
 * This XSLT element can only be used within the element xsl:analyze-string.
 * 
 */
public class ElemMatchingSubstring extends ElemTemplateElement implements ExpressionOwner {
  
  private static final long serialVersionUID = -5675647188821169056L;
  
  private String m_strValue = null;

  /**
   * Constructor of this class.
   */
  public ElemMatchingSubstring(){}

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException {
      super.compose(sroot);
  }
  
  /**
   * This after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException {
      super.endCompose(sroot);
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken() {
     return Constants.ELEMNAME_MATCHING_SUBSTRING;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName() {
     return Constants.ELEMNAME_MATCHINGSUBSTRING_STRING;
  }

  /**
   * Execute the xsl:matching-substring transformation
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException {
      transformSelectedNodes(transformer);
  }

  /**
   * Get template element associated with this
   *
   * @return template element associated with this (itself)
   */
  protected ElemTemplateElement getTemplateMatch() {
      return this;
  }

  /**
   * @param transformer             non-null reference to the the current transform-time state.
   *
   * @throws TransformerException   Thrown in a variety of circumstances.
   * 
   * @xsl.usage advanced
   */
  public void transformSelectedNodes(TransformerImpl transformer) throws TransformerException {

      final XPathContext xctxt = transformer.getXPathContext();
       
      for (ElemTemplateElement elem = this.m_firstChild; elem != null; 
                                                    elem = elem.m_nextSibling) {
          xctxt.setSAXLocator(elem);
          transformer.setCurrentElement(elem);
          elem.execute(transformer);
      }
  }

  /**

   * @param newChild Child to add to child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild) {
      return super.appendChild(newChild);
  }
  
  /**
   * Call the children visitors.
   * 
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes) {
      super.callChildVisitors(visitor, callAttributes);
  }

  @Override
  public Expression getExpression() {
      return null;
  }

  @Override
  public void setExpression(Expression exp) {
      // NO OP 
  }

  public String getStrValue() {
      return m_strValue;
  }

  public void setStrValue(String m_strValue) {
      this.m_strValue = m_strValue;
  }

}
