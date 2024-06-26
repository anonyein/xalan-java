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
package org.apache.xalan3.xslt3;

import org.apache.xalan3.xpath3.FnAbsTests;
import org.apache.xalan3.xpath3.FnBaseUriTests;
import org.apache.xalan3.xpath3.FnDataTests;
import org.apache.xalan3.xpath3.FnDateTimeTests;
import org.apache.xalan3.xpath3.FnDeepEqualTests;
import org.apache.xalan3.xpath3.FnDefaultCollation;
import org.apache.xalan3.xpath3.FnDocTests;
import org.apache.xalan3.xpath3.FnDocumentUriTests;
import org.apache.xalan3.xpath3.FnForEachTests;
import org.apache.xalan3.xpath3.FnLangTests;
import org.apache.xalan3.xpath3.FnMapMerge;
import org.apache.xalan3.xpath3.FnParseJsonTests;
import org.apache.xalan3.xpath3.FnQNameTests;
import org.apache.xalan3.xpath3.FnResolveQNameTests;
import org.apache.xalan3.xpath3.FnRootTests;
import org.apache.xalan3.xpath3.InlineFunctionItemExprTests;
import org.apache.xalan3.xpath3.StringTests;
import org.apache.xalan3.xpath3.ValueComparisonTests;
import org.apache.xalan3.xpath3.XPathArrayTests;
import org.apache.xalan3.xpath3.XPathArrowOpTests;
import org.apache.xalan3.xpath3.XPathCastTests;
import org.apache.xalan3.xpath3.XPathMapTests;
import org.apache.xalan3.xpath3.XsConstructorFunctionTests;
import org.apache.xalan3.xpath3.XsDateTimeArithmeticTests;
import org.apache.xalan3.xpath3.XsDateTimeTests;
import org.apache.xalan3.xpath3.XsDurationSubtypes;
import org.apache.xalan3.xpath3.XsTimeWithArithmeticTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * An JUnit test suite, for XSLT 3.0 and XPath 3.1 
 * language tests.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
@RunWith(Suite.class)
@SuiteClasses({ FnDocTests.class, FnDataTests.class, RecursiveFunctionTests.class,
                XslFunctionTests.class, HigherOrderFunctionTests.class, XsDateTimeTests.class,
                ValueComparisonTests.class, InlineFunctionItemExprTests.class, 
                FnForEachTests.class, XsConstructorFunctionTests.class,
                FnAbsTests.class, StringTests.class, XsDateTimeArithmeticTests.class,
                XsTimeWithArithmeticTests.class, XsDurationSubtypes.class,
                FnDeepEqualTests.class, XSLImportSchemaTests.class, FnDateTimeTests.class,
                XmlDotComHigherOrderFunctionsTests.class, FnDefaultCollation.class,
                FnBaseUriTests.class, FnDocumentUriTests.class, FnResolveQNameTests.class,
                FnQNameTests.class, XPathMapTests.class, XPathArrayTests.class, FnLangTests.class, 
                FnRootTests.class, FnParseJsonTests.class, XPathCastTests.class,
                XPathArrowOpTests.class, FnMapMerge.class })
public class Xsl3TestSuite1 {

}
