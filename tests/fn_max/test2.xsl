<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
   
    <!-- use with test1_a.xml -->
   
    <!-- An XSLT stylesheet test, to test the XPath 3.1 fn:max() 
         function.
         
         This stylesheet, gets an input data to be transformed
         from an XML external document.
    -->                 
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/info">
       <result>
         <one>
           <xsl:value-of select="max(*[not(self::data)])"/> 
         </one>
         <two>
           <xsl:value-of select="max(data/@*)"/>
         </two>
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