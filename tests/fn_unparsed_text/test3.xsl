<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->

   <xsl:output method="html" indent="yes"/>
   
   <xsl:variable name="fileContents" select="unparsed-text('test_1.txt')"/>

   <xsl:template match="/">
     <html>
        <head>
          <title/>
        </head>
        <body>
           <xsl:variable name="tokens">
              <xsl:for-each select="tokenize($fileContents, '\r?\n')">
                <token><xsl:value-of select="."/></token>
              </xsl:for-each>
           </xsl:variable>
           <xsl:for-each select="$tokens/token">
              <xsl:choose>
	         <xsl:when test="position() &lt; last()">
	            <b><xsl:value-of select="string(.)"/></b>
	            <br/>
	         </xsl:when>
	         <xsl:otherwise>
	            <b><xsl:value-of select="string(.)"/></b>
	         </xsl:otherwise>
               </xsl:choose>
           </xsl:for-each>
        </body>
      </html>
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