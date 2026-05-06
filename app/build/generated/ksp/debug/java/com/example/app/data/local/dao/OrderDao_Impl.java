package com.example.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.app.data.local.entities.OrderEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class OrderDao_Impl implements OrderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<OrderEntity> __insertionAdapterOfOrderEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOrderById;

  public OrderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOrderEntity = new EntityInsertionAdapter<OrderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `orders` (`id`,`title`,`clientName`,`filament`,`printer`,`printTimeHours`,`printTimeMinutes`,`weightGramsPerUnit`,`quantity`,`designType`,`color`,`isFriend`,`status`,`createdAt`,`totalComputed`,`quoteDate`,`validityDays`,`deliveryBusinessDays`,`deliveryDate`,`paymentType`,`depositPercentage`,`finishType`,`notes`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final OrderEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getClientName());
        statement.bindString(4, entity.getFilament());
        statement.bindString(5, entity.getPrinter());
        statement.bindLong(6, entity.getPrintTimeHours());
        statement.bindLong(7, entity.getPrintTimeMinutes());
        statement.bindLong(8, entity.getWeightGramsPerUnit());
        statement.bindLong(9, entity.getQuantity());
        statement.bindString(10, entity.getDesignType());
        statement.bindString(11, entity.getColor());
        final int _tmp = entity.isFriend() ? 1 : 0;
        statement.bindLong(12, _tmp);
        statement.bindString(13, entity.getStatus());
        statement.bindString(14, entity.getCreatedAt());
        statement.bindDouble(15, entity.getTotalComputed());
        statement.bindString(16, entity.getQuoteDate());
        statement.bindLong(17, entity.getValidityDays());
        statement.bindLong(18, entity.getDeliveryBusinessDays());
        statement.bindString(19, entity.getDeliveryDate());
        statement.bindString(20, entity.getPaymentType());
        statement.bindLong(21, entity.getDepositPercentage());
        statement.bindString(22, entity.getFinishType());
        statement.bindString(23, entity.getNotes());
      }
    };
    this.__preparedStmtOfDeleteOrderById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM orders WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertOrder(final OrderEntity order, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfOrderEntity.insert(order);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOrderById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOrderById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOrderById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<OrderEntity>> observeOrders() {
    final String _sql = "SELECT * FROM orders ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"orders"}, new Callable<List<OrderEntity>>() {
      @Override
      @NonNull
      public List<OrderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfClientName = CursorUtil.getColumnIndexOrThrow(_cursor, "clientName");
          final int _cursorIndexOfFilament = CursorUtil.getColumnIndexOrThrow(_cursor, "filament");
          final int _cursorIndexOfPrinter = CursorUtil.getColumnIndexOrThrow(_cursor, "printer");
          final int _cursorIndexOfPrintTimeHours = CursorUtil.getColumnIndexOrThrow(_cursor, "printTimeHours");
          final int _cursorIndexOfPrintTimeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "printTimeMinutes");
          final int _cursorIndexOfWeightGramsPerUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "weightGramsPerUnit");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfDesignType = CursorUtil.getColumnIndexOrThrow(_cursor, "designType");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfIsFriend = CursorUtil.getColumnIndexOrThrow(_cursor, "isFriend");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfTotalComputed = CursorUtil.getColumnIndexOrThrow(_cursor, "totalComputed");
          final int _cursorIndexOfQuoteDate = CursorUtil.getColumnIndexOrThrow(_cursor, "quoteDate");
          final int _cursorIndexOfValidityDays = CursorUtil.getColumnIndexOrThrow(_cursor, "validityDays");
          final int _cursorIndexOfDeliveryBusinessDays = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryBusinessDays");
          final int _cursorIndexOfDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryDate");
          final int _cursorIndexOfPaymentType = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentType");
          final int _cursorIndexOfDepositPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "depositPercentage");
          final int _cursorIndexOfFinishType = CursorUtil.getColumnIndexOrThrow(_cursor, "finishType");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<OrderEntity> _result = new ArrayList<OrderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OrderEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpClientName;
            _tmpClientName = _cursor.getString(_cursorIndexOfClientName);
            final String _tmpFilament;
            _tmpFilament = _cursor.getString(_cursorIndexOfFilament);
            final String _tmpPrinter;
            _tmpPrinter = _cursor.getString(_cursorIndexOfPrinter);
            final int _tmpPrintTimeHours;
            _tmpPrintTimeHours = _cursor.getInt(_cursorIndexOfPrintTimeHours);
            final int _tmpPrintTimeMinutes;
            _tmpPrintTimeMinutes = _cursor.getInt(_cursorIndexOfPrintTimeMinutes);
            final int _tmpWeightGramsPerUnit;
            _tmpWeightGramsPerUnit = _cursor.getInt(_cursorIndexOfWeightGramsPerUnit);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final String _tmpDesignType;
            _tmpDesignType = _cursor.getString(_cursorIndexOfDesignType);
            final String _tmpColor;
            _tmpColor = _cursor.getString(_cursorIndexOfColor);
            final boolean _tmpIsFriend;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFriend);
            _tmpIsFriend = _tmp != 0;
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            final double _tmpTotalComputed;
            _tmpTotalComputed = _cursor.getDouble(_cursorIndexOfTotalComputed);
            final String _tmpQuoteDate;
            _tmpQuoteDate = _cursor.getString(_cursorIndexOfQuoteDate);
            final int _tmpValidityDays;
            _tmpValidityDays = _cursor.getInt(_cursorIndexOfValidityDays);
            final int _tmpDeliveryBusinessDays;
            _tmpDeliveryBusinessDays = _cursor.getInt(_cursorIndexOfDeliveryBusinessDays);
            final String _tmpDeliveryDate;
            _tmpDeliveryDate = _cursor.getString(_cursorIndexOfDeliveryDate);
            final String _tmpPaymentType;
            _tmpPaymentType = _cursor.getString(_cursorIndexOfPaymentType);
            final int _tmpDepositPercentage;
            _tmpDepositPercentage = _cursor.getInt(_cursorIndexOfDepositPercentage);
            final String _tmpFinishType;
            _tmpFinishType = _cursor.getString(_cursorIndexOfFinishType);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new OrderEntity(_tmpId,_tmpTitle,_tmpClientName,_tmpFilament,_tmpPrinter,_tmpPrintTimeHours,_tmpPrintTimeMinutes,_tmpWeightGramsPerUnit,_tmpQuantity,_tmpDesignType,_tmpColor,_tmpIsFriend,_tmpStatus,_tmpCreatedAt,_tmpTotalComputed,_tmpQuoteDate,_tmpValidityDays,_tmpDeliveryBusinessDays,_tmpDeliveryDate,_tmpPaymentType,_tmpDepositPercentage,_tmpFinishType,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
