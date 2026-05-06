package com.example.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.app.data.local.entities.QuoteSettingsEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SettingsDao_Impl implements SettingsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<QuoteSettingsEntity> __insertionAdapterOfQuoteSettingsEntity;

  public SettingsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQuoteSettingsEntity = new EntityInsertionAdapter<QuoteSettingsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `quote_settings` (`id`,`filamentPlaCost`,`filamentPetgCost`,`filamentFlexCost`,`energyRate`,`marginPercentage`,`friendDiscountPercentage`,`a1ComboPrice`,`a1ComboLifespanHours`,`a1ComboPowerKw`,`a1MiniPrice`,`a1MiniLifespanHours`,`a1MiniPowerKw`,`discount25`,`discount50`,`discount75`,`discount100`,`designExternalCost`,`designOwnCost`,`designDetailsCost`,`designModificationsCost`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuoteSettingsEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getFilamentPlaCost());
        statement.bindDouble(3, entity.getFilamentPetgCost());
        statement.bindDouble(4, entity.getFilamentFlexCost());
        statement.bindDouble(5, entity.getEnergyRate());
        statement.bindDouble(6, entity.getMarginPercentage());
        statement.bindDouble(7, entity.getFriendDiscountPercentage());
        statement.bindDouble(8, entity.getA1ComboPrice());
        statement.bindLong(9, entity.getA1ComboLifespanHours());
        statement.bindDouble(10, entity.getA1ComboPowerKw());
        statement.bindDouble(11, entity.getA1MiniPrice());
        statement.bindLong(12, entity.getA1MiniLifespanHours());
        statement.bindDouble(13, entity.getA1MiniPowerKw());
        statement.bindDouble(14, entity.getDiscount25());
        statement.bindDouble(15, entity.getDiscount50());
        statement.bindDouble(16, entity.getDiscount75());
        statement.bindDouble(17, entity.getDiscount100());
        statement.bindDouble(18, entity.getDesignExternalCost());
        statement.bindDouble(19, entity.getDesignOwnCost());
        statement.bindDouble(20, entity.getDesignDetailsCost());
        statement.bindDouble(21, entity.getDesignModificationsCost());
      }
    };
  }

  @Override
  public Object upsertSettings(final QuoteSettingsEntity settings,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQuoteSettingsEntity.insert(settings);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<QuoteSettingsEntity> observeSettings() {
    final String _sql = "SELECT * FROM quote_settings WHERE id = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"quote_settings"}, new Callable<QuoteSettingsEntity>() {
      @Override
      @Nullable
      public QuoteSettingsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFilamentPlaCost = CursorUtil.getColumnIndexOrThrow(_cursor, "filamentPlaCost");
          final int _cursorIndexOfFilamentPetgCost = CursorUtil.getColumnIndexOrThrow(_cursor, "filamentPetgCost");
          final int _cursorIndexOfFilamentFlexCost = CursorUtil.getColumnIndexOrThrow(_cursor, "filamentFlexCost");
          final int _cursorIndexOfEnergyRate = CursorUtil.getColumnIndexOrThrow(_cursor, "energyRate");
          final int _cursorIndexOfMarginPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "marginPercentage");
          final int _cursorIndexOfFriendDiscountPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "friendDiscountPercentage");
          final int _cursorIndexOfA1ComboPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "a1ComboPrice");
          final int _cursorIndexOfA1ComboLifespanHours = CursorUtil.getColumnIndexOrThrow(_cursor, "a1ComboLifespanHours");
          final int _cursorIndexOfA1ComboPowerKw = CursorUtil.getColumnIndexOrThrow(_cursor, "a1ComboPowerKw");
          final int _cursorIndexOfA1MiniPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "a1MiniPrice");
          final int _cursorIndexOfA1MiniLifespanHours = CursorUtil.getColumnIndexOrThrow(_cursor, "a1MiniLifespanHours");
          final int _cursorIndexOfA1MiniPowerKw = CursorUtil.getColumnIndexOrThrow(_cursor, "a1MiniPowerKw");
          final int _cursorIndexOfDiscount25 = CursorUtil.getColumnIndexOrThrow(_cursor, "discount25");
          final int _cursorIndexOfDiscount50 = CursorUtil.getColumnIndexOrThrow(_cursor, "discount50");
          final int _cursorIndexOfDiscount75 = CursorUtil.getColumnIndexOrThrow(_cursor, "discount75");
          final int _cursorIndexOfDiscount100 = CursorUtil.getColumnIndexOrThrow(_cursor, "discount100");
          final int _cursorIndexOfDesignExternalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "designExternalCost");
          final int _cursorIndexOfDesignOwnCost = CursorUtil.getColumnIndexOrThrow(_cursor, "designOwnCost");
          final int _cursorIndexOfDesignDetailsCost = CursorUtil.getColumnIndexOrThrow(_cursor, "designDetailsCost");
          final int _cursorIndexOfDesignModificationsCost = CursorUtil.getColumnIndexOrThrow(_cursor, "designModificationsCost");
          final QuoteSettingsEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final double _tmpFilamentPlaCost;
            _tmpFilamentPlaCost = _cursor.getDouble(_cursorIndexOfFilamentPlaCost);
            final double _tmpFilamentPetgCost;
            _tmpFilamentPetgCost = _cursor.getDouble(_cursorIndexOfFilamentPetgCost);
            final double _tmpFilamentFlexCost;
            _tmpFilamentFlexCost = _cursor.getDouble(_cursorIndexOfFilamentFlexCost);
            final double _tmpEnergyRate;
            _tmpEnergyRate = _cursor.getDouble(_cursorIndexOfEnergyRate);
            final double _tmpMarginPercentage;
            _tmpMarginPercentage = _cursor.getDouble(_cursorIndexOfMarginPercentage);
            final double _tmpFriendDiscountPercentage;
            _tmpFriendDiscountPercentage = _cursor.getDouble(_cursorIndexOfFriendDiscountPercentage);
            final double _tmpA1ComboPrice;
            _tmpA1ComboPrice = _cursor.getDouble(_cursorIndexOfA1ComboPrice);
            final int _tmpA1ComboLifespanHours;
            _tmpA1ComboLifespanHours = _cursor.getInt(_cursorIndexOfA1ComboLifespanHours);
            final double _tmpA1ComboPowerKw;
            _tmpA1ComboPowerKw = _cursor.getDouble(_cursorIndexOfA1ComboPowerKw);
            final double _tmpA1MiniPrice;
            _tmpA1MiniPrice = _cursor.getDouble(_cursorIndexOfA1MiniPrice);
            final int _tmpA1MiniLifespanHours;
            _tmpA1MiniLifespanHours = _cursor.getInt(_cursorIndexOfA1MiniLifespanHours);
            final double _tmpA1MiniPowerKw;
            _tmpA1MiniPowerKw = _cursor.getDouble(_cursorIndexOfA1MiniPowerKw);
            final double _tmpDiscount25;
            _tmpDiscount25 = _cursor.getDouble(_cursorIndexOfDiscount25);
            final double _tmpDiscount50;
            _tmpDiscount50 = _cursor.getDouble(_cursorIndexOfDiscount50);
            final double _tmpDiscount75;
            _tmpDiscount75 = _cursor.getDouble(_cursorIndexOfDiscount75);
            final double _tmpDiscount100;
            _tmpDiscount100 = _cursor.getDouble(_cursorIndexOfDiscount100);
            final double _tmpDesignExternalCost;
            _tmpDesignExternalCost = _cursor.getDouble(_cursorIndexOfDesignExternalCost);
            final double _tmpDesignOwnCost;
            _tmpDesignOwnCost = _cursor.getDouble(_cursorIndexOfDesignOwnCost);
            final double _tmpDesignDetailsCost;
            _tmpDesignDetailsCost = _cursor.getDouble(_cursorIndexOfDesignDetailsCost);
            final double _tmpDesignModificationsCost;
            _tmpDesignModificationsCost = _cursor.getDouble(_cursorIndexOfDesignModificationsCost);
            _result = new QuoteSettingsEntity(_tmpId,_tmpFilamentPlaCost,_tmpFilamentPetgCost,_tmpFilamentFlexCost,_tmpEnergyRate,_tmpMarginPercentage,_tmpFriendDiscountPercentage,_tmpA1ComboPrice,_tmpA1ComboLifespanHours,_tmpA1ComboPowerKw,_tmpA1MiniPrice,_tmpA1MiniLifespanHours,_tmpA1MiniPowerKw,_tmpDiscount25,_tmpDiscount50,_tmpDiscount75,_tmpDiscount100,_tmpDesignExternalCost,_tmpDesignOwnCost,_tmpDesignDetailsCost,_tmpDesignModificationsCost);
          } else {
            _result = null;
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

  @Override
  public Object getSettingsOnce(final Continuation<? super QuoteSettingsEntity> $completion) {
    final String _sql = "SELECT * FROM quote_settings WHERE id = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<QuoteSettingsEntity>() {
      @Override
      @Nullable
      public QuoteSettingsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFilamentPlaCost = CursorUtil.getColumnIndexOrThrow(_cursor, "filamentPlaCost");
          final int _cursorIndexOfFilamentPetgCost = CursorUtil.getColumnIndexOrThrow(_cursor, "filamentPetgCost");
          final int _cursorIndexOfFilamentFlexCost = CursorUtil.getColumnIndexOrThrow(_cursor, "filamentFlexCost");
          final int _cursorIndexOfEnergyRate = CursorUtil.getColumnIndexOrThrow(_cursor, "energyRate");
          final int _cursorIndexOfMarginPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "marginPercentage");
          final int _cursorIndexOfFriendDiscountPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "friendDiscountPercentage");
          final int _cursorIndexOfA1ComboPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "a1ComboPrice");
          final int _cursorIndexOfA1ComboLifespanHours = CursorUtil.getColumnIndexOrThrow(_cursor, "a1ComboLifespanHours");
          final int _cursorIndexOfA1ComboPowerKw = CursorUtil.getColumnIndexOrThrow(_cursor, "a1ComboPowerKw");
          final int _cursorIndexOfA1MiniPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "a1MiniPrice");
          final int _cursorIndexOfA1MiniLifespanHours = CursorUtil.getColumnIndexOrThrow(_cursor, "a1MiniLifespanHours");
          final int _cursorIndexOfA1MiniPowerKw = CursorUtil.getColumnIndexOrThrow(_cursor, "a1MiniPowerKw");
          final int _cursorIndexOfDiscount25 = CursorUtil.getColumnIndexOrThrow(_cursor, "discount25");
          final int _cursorIndexOfDiscount50 = CursorUtil.getColumnIndexOrThrow(_cursor, "discount50");
          final int _cursorIndexOfDiscount75 = CursorUtil.getColumnIndexOrThrow(_cursor, "discount75");
          final int _cursorIndexOfDiscount100 = CursorUtil.getColumnIndexOrThrow(_cursor, "discount100");
          final int _cursorIndexOfDesignExternalCost = CursorUtil.getColumnIndexOrThrow(_cursor, "designExternalCost");
          final int _cursorIndexOfDesignOwnCost = CursorUtil.getColumnIndexOrThrow(_cursor, "designOwnCost");
          final int _cursorIndexOfDesignDetailsCost = CursorUtil.getColumnIndexOrThrow(_cursor, "designDetailsCost");
          final int _cursorIndexOfDesignModificationsCost = CursorUtil.getColumnIndexOrThrow(_cursor, "designModificationsCost");
          final QuoteSettingsEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final double _tmpFilamentPlaCost;
            _tmpFilamentPlaCost = _cursor.getDouble(_cursorIndexOfFilamentPlaCost);
            final double _tmpFilamentPetgCost;
            _tmpFilamentPetgCost = _cursor.getDouble(_cursorIndexOfFilamentPetgCost);
            final double _tmpFilamentFlexCost;
            _tmpFilamentFlexCost = _cursor.getDouble(_cursorIndexOfFilamentFlexCost);
            final double _tmpEnergyRate;
            _tmpEnergyRate = _cursor.getDouble(_cursorIndexOfEnergyRate);
            final double _tmpMarginPercentage;
            _tmpMarginPercentage = _cursor.getDouble(_cursorIndexOfMarginPercentage);
            final double _tmpFriendDiscountPercentage;
            _tmpFriendDiscountPercentage = _cursor.getDouble(_cursorIndexOfFriendDiscountPercentage);
            final double _tmpA1ComboPrice;
            _tmpA1ComboPrice = _cursor.getDouble(_cursorIndexOfA1ComboPrice);
            final int _tmpA1ComboLifespanHours;
            _tmpA1ComboLifespanHours = _cursor.getInt(_cursorIndexOfA1ComboLifespanHours);
            final double _tmpA1ComboPowerKw;
            _tmpA1ComboPowerKw = _cursor.getDouble(_cursorIndexOfA1ComboPowerKw);
            final double _tmpA1MiniPrice;
            _tmpA1MiniPrice = _cursor.getDouble(_cursorIndexOfA1MiniPrice);
            final int _tmpA1MiniLifespanHours;
            _tmpA1MiniLifespanHours = _cursor.getInt(_cursorIndexOfA1MiniLifespanHours);
            final double _tmpA1MiniPowerKw;
            _tmpA1MiniPowerKw = _cursor.getDouble(_cursorIndexOfA1MiniPowerKw);
            final double _tmpDiscount25;
            _tmpDiscount25 = _cursor.getDouble(_cursorIndexOfDiscount25);
            final double _tmpDiscount50;
            _tmpDiscount50 = _cursor.getDouble(_cursorIndexOfDiscount50);
            final double _tmpDiscount75;
            _tmpDiscount75 = _cursor.getDouble(_cursorIndexOfDiscount75);
            final double _tmpDiscount100;
            _tmpDiscount100 = _cursor.getDouble(_cursorIndexOfDiscount100);
            final double _tmpDesignExternalCost;
            _tmpDesignExternalCost = _cursor.getDouble(_cursorIndexOfDesignExternalCost);
            final double _tmpDesignOwnCost;
            _tmpDesignOwnCost = _cursor.getDouble(_cursorIndexOfDesignOwnCost);
            final double _tmpDesignDetailsCost;
            _tmpDesignDetailsCost = _cursor.getDouble(_cursorIndexOfDesignDetailsCost);
            final double _tmpDesignModificationsCost;
            _tmpDesignModificationsCost = _cursor.getDouble(_cursorIndexOfDesignModificationsCost);
            _result = new QuoteSettingsEntity(_tmpId,_tmpFilamentPlaCost,_tmpFilamentPetgCost,_tmpFilamentFlexCost,_tmpEnergyRate,_tmpMarginPercentage,_tmpFriendDiscountPercentage,_tmpA1ComboPrice,_tmpA1ComboLifespanHours,_tmpA1ComboPowerKw,_tmpA1MiniPrice,_tmpA1MiniLifespanHours,_tmpA1MiniPowerKw,_tmpDiscount25,_tmpDiscount50,_tmpDiscount75,_tmpDiscount100,_tmpDesignExternalCost,_tmpDesignOwnCost,_tmpDesignDetailsCost,_tmpDesignModificationsCost);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
