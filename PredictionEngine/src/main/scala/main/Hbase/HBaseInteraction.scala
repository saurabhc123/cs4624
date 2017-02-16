package main.Hbase


import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.filter.Filter
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter
import org.apache.hadoop.hbase.filter.CompareFilter
import org.apache.hadoop.hbase.filter.BinaryComparator

  @SerialVersionUID(107L)
  class HBaseInteraction(tableName: String) extends Serializable{
    val tn = tableName

    // Get connection to HBase Table using connection factory
    // https://hbase.apache.org/devapidocs/org/apache/hadoop/hbase/client/HTable.html
    val connection = ConnectionFactory.createConnection()
    val table = connection.getTable(TableName.valueOf(tn))
    def close(): Unit= {
      table.close()
      connection.close()
    }
    def putValueAt(columnFamily: String, column: String, rowKey: String, value: String) = {
      // Make a new put object to handle adding data to the table
      // https://hbase.apache.org/apidocs/org/apache/hadoop/hbase/client/Put.html
      val put = new Put(Bytes.toBytes(rowKey))

      // add data to the put
      put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(column), Bytes.toBytes(value))

      // put the data in the table
      table.put(put)
    }

    def put(put: Put): Unit ={
      table.put(put)
    }
    def get(get:Get): Result = {
      table.get(get)
    }

    def getValueAt(columnFamily: String, column: String, rowKey: String): String = {
      // Make a new get object to handle getting the row from the table
      // https://hbase.apache.org/apidocs/org/apache/hadoop/hbase/client/Get.html
      val get = new Get(Bytes.toBytes(rowKey))

      // get info from the table, returns a Result object
      // https://hbase.apache.org/devapidocs/org/apache/hadoop/hbase/client/Result.html
      val result = table.get(get)

      // get the value out of the desired column and return
      val finalResult = Bytes.toString(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(column)))
      return finalResult
    }

    def getValuesBasedOnColumn(value: String, columnFamily: String, column: String): ResultScanner = {
      // Make a new Scan object
      // https://hbase.apache.org/apidocs/org/apache/hadoop/hbase/client/Scan.html
      val scanner = new Scan()

      // tell the scanner which column to grab
      scanner.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column))
      // Filter according to the collection number
      scanner.setFilter(new SingleColumnValueFilter(Bytes.toBytes(columnFamily), Bytes.toBytes(column), CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(value))))

      // Get the scanner back from the table
      // https://hbase.apache.org/devapidocs/org/apache/hadoop/hbase/client/ResultScanner.html
      // Note that resultScanner implements iterable, so you can process individual elements that way
      val resultScanner = table.getScanner(scanner)

      return resultScanner
    }

    def getRowsBetweenPrefix(prefix: String, columnFamily: String, column: String): ResultScanner = {
      val scanner = new Scan(Bytes.toBytes(prefix), Bytes.toBytes(prefix + '0'))
      scanner.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column))
      table.getScanner(scanner)
    }
}

/*val hbase = new HBaseInteraction("hbase_class_demo")

// Read a value from a single cell in the table
// column family, column, row key
//println(hbase.getValueAt("cf1", "c2", "3"))

// Put a value in a single cell in the table
// column family, column, row key, value
//hbase.putValueAt("cf2", "c2", "10", "strange data")

// Add a bunch of things to this new column

hbase.putValueAt("cf2", "c2", "1", "yes")
hbase.putValueAt("cf2", "c2", "2", "yes")
hbase.putValueAt("cf2", "c2", "3", "no")
hbase.putValueAt("cf2", "c2", "4", "yes")


// Read a collection of values based on some column's value

val tableScanner = hbase.getValuesBasedOnColumn("yes", "cf2", "c2")
val tsIterator = tableScanner.iterator
while(tsIterator.hasNext) {
	println(tsIterator.next())
}*/

