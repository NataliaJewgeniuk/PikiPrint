package com.example.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.app.data.local.dao.ExpenseDao;
import com.example.app.data.local.dao.ExpenseDao_Impl;
import com.example.app.data.local.dao.OrderDao;
import com.example.app.data.local.dao.OrderDao_Impl;
import com.example.app.data.local.dao.SettingsDao;
import com.example.app.data.local.dao.SettingsDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PikiPrintDatabase_Impl extends PikiPrintDatabase {
  private volatile OrderDao _orderDao;

  private volatile ExpenseDao _expenseDao;

  private volatile SettingsDao _settingsDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `orders` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `clientName` TEXT NOT NULL, `filament` TEXT NOT NULL, `printer` TEXT NOT NULL, `printTimeHours` INTEGER NOT NULL, `printTimeMinutes` INTEGER NOT NULL, `weightGramsPerUnit` INTEGER NOT NULL, `quantity` INTEGER NOT NULL, `designType` TEXT NOT NULL, `color` TEXT NOT NULL, `isFriend` INTEGER NOT NULL, `status` TEXT NOT NULL, `createdAt` TEXT NOT NULL, `totalComputed` REAL NOT NULL, `quoteDate` TEXT NOT NULL, `validityDays` INTEGER NOT NULL, `deliveryBusinessDays` INTEGER NOT NULL, `deliveryDate` TEXT NOT NULL, `paymentType` TEXT NOT NULL, `depositPercentage` INTEGER NOT NULL, `finishType` TEXT NOT NULL, `notes` TEXT NOT NULL, `subtotal` REAL NOT NULL, `margenMonto` REAL NOT NULL, `descuentoCantidad` REAL NOT NULL, `descuentoAmigo` REAL NOT NULL, `totalFinal` REAL NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `expenses` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `amount` REAL NOT NULL, `date` TEXT NOT NULL, `category` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `quote_settings` (`id` INTEGER NOT NULL, `filamentPlaCost` REAL NOT NULL, `filamentPetgCost` REAL NOT NULL, `filamentFlexCost` REAL NOT NULL, `energyRate` REAL NOT NULL, `marginPercentage` REAL NOT NULL, `friendDiscountPercentage` REAL NOT NULL, `a1ComboPrice` REAL NOT NULL, `a1ComboLifespanHours` INTEGER NOT NULL, `a1ComboPowerKw` REAL NOT NULL, `a1MiniPrice` REAL NOT NULL, `a1MiniLifespanHours` INTEGER NOT NULL, `a1MiniPowerKw` REAL NOT NULL, `discount25` REAL NOT NULL, `discount50` REAL NOT NULL, `discount75` REAL NOT NULL, `discount100` REAL NOT NULL, `designExternalCost` REAL NOT NULL, `designOwnCost` REAL NOT NULL, `designDetailsCost` REAL NOT NULL, `designModificationsCost` REAL NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8a7efca2844b0a23b78c5802828a125a')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `orders`");
        db.execSQL("DROP TABLE IF EXISTS `expenses`");
        db.execSQL("DROP TABLE IF EXISTS `quote_settings`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsOrders = new HashMap<String, TableInfo.Column>(28);
        _columnsOrders.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("clientName", new TableInfo.Column("clientName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("filament", new TableInfo.Column("filament", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("printer", new TableInfo.Column("printer", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("printTimeHours", new TableInfo.Column("printTimeHours", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("printTimeMinutes", new TableInfo.Column("printTimeMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("weightGramsPerUnit", new TableInfo.Column("weightGramsPerUnit", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("designType", new TableInfo.Column("designType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("color", new TableInfo.Column("color", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("isFriend", new TableInfo.Column("isFriend", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("createdAt", new TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("totalComputed", new TableInfo.Column("totalComputed", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("quoteDate", new TableInfo.Column("quoteDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("validityDays", new TableInfo.Column("validityDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("deliveryBusinessDays", new TableInfo.Column("deliveryBusinessDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("deliveryDate", new TableInfo.Column("deliveryDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("paymentType", new TableInfo.Column("paymentType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("depositPercentage", new TableInfo.Column("depositPercentage", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("finishType", new TableInfo.Column("finishType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("subtotal", new TableInfo.Column("subtotal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("margenMonto", new TableInfo.Column("margenMonto", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("descuentoCantidad", new TableInfo.Column("descuentoCantidad", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("descuentoAmigo", new TableInfo.Column("descuentoAmigo", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOrders.put("totalFinal", new TableInfo.Column("totalFinal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOrders = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesOrders = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoOrders = new TableInfo("orders", _columnsOrders, _foreignKeysOrders, _indicesOrders);
        final TableInfo _existingOrders = TableInfo.read(db, "orders");
        if (!_infoOrders.equals(_existingOrders)) {
          return new RoomOpenHelper.ValidationResult(false, "orders(com.example.app.data.local.entities.OrderEntity).\n"
                  + " Expected:\n" + _infoOrders + "\n"
                  + " Found:\n" + _existingOrders);
        }
        final HashMap<String, TableInfo.Column> _columnsExpenses = new HashMap<String, TableInfo.Column>(5);
        _columnsExpenses.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExpenses = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExpenses = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExpenses = new TableInfo("expenses", _columnsExpenses, _foreignKeysExpenses, _indicesExpenses);
        final TableInfo _existingExpenses = TableInfo.read(db, "expenses");
        if (!_infoExpenses.equals(_existingExpenses)) {
          return new RoomOpenHelper.ValidationResult(false, "expenses(com.example.app.data.local.entities.ExpenseEntity).\n"
                  + " Expected:\n" + _infoExpenses + "\n"
                  + " Found:\n" + _existingExpenses);
        }
        final HashMap<String, TableInfo.Column> _columnsQuoteSettings = new HashMap<String, TableInfo.Column>(21);
        _columnsQuoteSettings.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("filamentPlaCost", new TableInfo.Column("filamentPlaCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("filamentPetgCost", new TableInfo.Column("filamentPetgCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("filamentFlexCost", new TableInfo.Column("filamentFlexCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("energyRate", new TableInfo.Column("energyRate", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("marginPercentage", new TableInfo.Column("marginPercentage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("friendDiscountPercentage", new TableInfo.Column("friendDiscountPercentage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("a1ComboPrice", new TableInfo.Column("a1ComboPrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("a1ComboLifespanHours", new TableInfo.Column("a1ComboLifespanHours", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("a1ComboPowerKw", new TableInfo.Column("a1ComboPowerKw", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("a1MiniPrice", new TableInfo.Column("a1MiniPrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("a1MiniLifespanHours", new TableInfo.Column("a1MiniLifespanHours", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("a1MiniPowerKw", new TableInfo.Column("a1MiniPowerKw", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("discount25", new TableInfo.Column("discount25", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("discount50", new TableInfo.Column("discount50", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("discount75", new TableInfo.Column("discount75", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("discount100", new TableInfo.Column("discount100", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("designExternalCost", new TableInfo.Column("designExternalCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("designOwnCost", new TableInfo.Column("designOwnCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("designDetailsCost", new TableInfo.Column("designDetailsCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuoteSettings.put("designModificationsCost", new TableInfo.Column("designModificationsCost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuoteSettings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesQuoteSettings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoQuoteSettings = new TableInfo("quote_settings", _columnsQuoteSettings, _foreignKeysQuoteSettings, _indicesQuoteSettings);
        final TableInfo _existingQuoteSettings = TableInfo.read(db, "quote_settings");
        if (!_infoQuoteSettings.equals(_existingQuoteSettings)) {
          return new RoomOpenHelper.ValidationResult(false, "quote_settings(com.example.app.data.local.entities.QuoteSettingsEntity).\n"
                  + " Expected:\n" + _infoQuoteSettings + "\n"
                  + " Found:\n" + _existingQuoteSettings);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "8a7efca2844b0a23b78c5802828a125a", "5c54996e5f97a5309254a7f513f0aaa5");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "orders","expenses","quote_settings");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `orders`");
      _db.execSQL("DELETE FROM `expenses`");
      _db.execSQL("DELETE FROM `quote_settings`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(OrderDao.class, OrderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExpenseDao.class, ExpenseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SettingsDao.class, SettingsDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public OrderDao orderDao() {
    if (_orderDao != null) {
      return _orderDao;
    } else {
      synchronized(this) {
        if(_orderDao == null) {
          _orderDao = new OrderDao_Impl(this);
        }
        return _orderDao;
      }
    }
  }

  @Override
  public ExpenseDao expenseDao() {
    if (_expenseDao != null) {
      return _expenseDao;
    } else {
      synchronized(this) {
        if(_expenseDao == null) {
          _expenseDao = new ExpenseDao_Impl(this);
        }
        return _expenseDao;
      }
    }
  }

  @Override
  public SettingsDao settingsDao() {
    if (_settingsDao != null) {
      return _settingsDao;
    } else {
      synchronized(this) {
        if(_settingsDao == null) {
          _settingsDao = new SettingsDao_Impl(this);
        }
        return _settingsDao;
      }
    }
  }
}
