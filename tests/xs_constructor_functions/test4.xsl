<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_a.xml -->
   
   <!-- This XSLT stylesheet, tests few XPath arithmetic and 
        logical operations, involving xs:decimal types. -->             

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/elem">
      <result>
         <one><xsl:value-of select="(xs:decimal(a) + xs:decimal(b)) = xs:decimal(c)"/></one>
         <two><xsl:value-of select="(xs:decimal(a) + xs:decimal(b)) = xs:decimal(d)"/></two>
         <three><xsl:value-of select="(xs:decimal(a) + b) = c"/></three>
         <four><xsl:value-of select="(xs:decimal(a) + b) = d"/></four>
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