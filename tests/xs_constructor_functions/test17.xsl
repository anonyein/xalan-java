<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet test case, to test xs:string(..)
        constructor function, and an XPath 'lt' operator when 
        comparing string values. -->                              

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/">
     <result>
        <one><xsl:copy-of select="xs:string('aaa') lt xs:string('abc')"/></one>
        <two><xsl:copy-of select="xs:string('aaa') lt 'abc'"/></two>
        <three><xsl:copy-of select="xs:string('aaa') lt string('abc')"/></three>
       
        <four><xsl:copy-of select="xs:string('abc') lt xs:string('a')"/></four>
        <five><xsl:copy-of select="xs:string('abc') lt 'a'"/></five>
        <six><xsl:copy-of select="xs:string('abc') lt string('a')"/></six>
     </result>
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