<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_e.xml -->
   
   <!-- An XSLT stylesheet to test, xsl:iterate instruction,
        which breaks from xsl:iterate loop when a particular
        condition becomes true. -->               

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/elem">
      <result>
        <xsl:iterate select="item">
           <xsl:choose>
              <xsl:when test="number(@weight) eq 11">
                 <xsl:break/>
              </xsl:when>
              <xsl:otherwise>
                 <xsl:copy-of select="."/>
              </xsl:otherwise>
           </xsl:choose>
        </xsl:iterate>
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