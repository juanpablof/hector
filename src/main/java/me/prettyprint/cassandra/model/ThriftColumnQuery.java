package me.prettyprint.cassandra.model;

import me.prettyprint.cassandra.service.Keyspace;
import me.prettyprint.hector.api.query.ColumnQuery;

import org.apache.cassandra.thrift.Column;

/**
 * Thrift implementation of the ColumnQuery type.
 *
 * @author Ran Tavory
 *
 * @param <N> column name type
 * @param <V> value type
 */
public class ThriftColumnQuery<K, N, V> extends AbstractColumnQuery<K, N, V>
    implements ColumnQuery<K, N, V> {

  public ThriftColumnQuery(KeyspaceOperator keyspaceOperator, Serializer<K> keySerializer,
      Serializer<N> nameSerializer,
      Serializer<V> valueSerializer) {
    super(keyspaceOperator, keySerializer, nameSerializer, valueSerializer);
  }

  @Override
  public Result<HColumn<N, V>> execute() {
    return new Result<HColumn<N, V>>(keyspaceOperator.doExecute(
        new KeyspaceOperationCallback<HColumn<N, V>>() {

          @Override
          public HColumn<N, V> doInKeyspace(Keyspace ks) throws HectorException {
            try {
              Column thriftColumn = ks.getColumn(keySerializer.toBytes(key),
                  ThriftFactory.createColumnPath(columnFamilyName, name, columnNameSerializer));
              return new HColumn<N, V>(thriftColumn, columnNameSerializer, valueSerializer);
            } catch (NotFoundException e) {
              return null;
            }
          }
        }), this);
  }
}