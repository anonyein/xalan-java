<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_b.xml -->
   
   <!-- using result of XPath 3.1 fn:tokenize() function, to 
        loop with xsl:for-each element. -->

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/elem">
      <elem>
        <result1>
          <xsl:for-each select="tokenize(str1)">
             <token><xsl:value-of select="."/></token>
          </xsl:for-each>
        </result1>
        <result2>
	   <xsl:for-each select="tokenize(str1, '(ab)|(a)')">
	     <token><xsl:value-of select="."/></token>
	   </xsl:for-each>
        </result2>
        <result3>
           <xsl:for-each select="tokenize(str2, '\s+')">
	      <token><xsl:value-of select="."/></token>
           </xsl:for-each>
        </result3>
        <result4>
	   <xsl:for-each select="tokenize(str3, ',\s*')">
	      <token><xsl:value-of select="."/></token>	      
	   </xsl:for-each>
        </result4>
      </elem>
   </xsl:template>
   
   <!--
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
   -->

</xsl:stylesheet>