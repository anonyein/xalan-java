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

/*
 * An object of this class, is used to store information about
 * XPath 3.1 "let" expression's single variable binding (i.e, 
 * run-time information details related to the grammar fragment 
 * "$" VarName ":=" ExprSingle for a particular XPath "let" 
 * expression that's currently been evaluated). 
 *  
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class LetExprVarBinding {
    
    private String fVarName = null;
    
    private String fXpathExprStr = null;

    public String getVarName() {
        return fVarName;
    }

    public void setVarName(String varName) {
        this.fVarName = varName;
    }

    public String getXpathExprStr() {
        return fXpathExprStr;
    }

    public void setXpathExprStr(String xpathExprStr) {
        this.fXpathExprStr = xpathExprStr;
    }

}
