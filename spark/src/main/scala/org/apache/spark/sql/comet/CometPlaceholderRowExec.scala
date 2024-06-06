/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.spark.sql.comet

import org.apache.spark.rdd._
import org.apache.spark.sql.catalyst._
import org.apache.spark.sql.catalyst.expressions.{Attribute, DynamicPruningExpression, Expression, Literal}
import org.apache.spark.sql.execution._
import org.apache.spark.sql.execution.RDDScanExec
import org.apache.spark.sql.types.StructType

case class CometPlaceholderRowExec(originalPlan: SparkPlan, child: SparkPlan)
    extends LeafExecNode
    with CometPlan {

  override def output: Seq[Attribute] = child.output

  override protected def doExecute(): RDD[InternalRow] = {
    val rdd = child.execute()
    rdd.mapPartitionsInternal { iter =>
      new Iterator[InternalRow] {
        override def hasNext: Boolean = iter.hasNext
        override def next(): InternalRow = iter.next()
      }
    }
  }

}
