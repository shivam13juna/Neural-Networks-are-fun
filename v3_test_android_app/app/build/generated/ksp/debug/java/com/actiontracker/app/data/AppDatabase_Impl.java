package com.actiontracker.app.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ActionDao _actionDao;

  private volatile DayRecordDao _dayRecordDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `actions` (`actionId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `actionName` TEXT NOT NULL, `creationTimestamp` INTEGER NOT NULL, `backgroundColor` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `day_records` (`date` TEXT NOT NULL, `actionId` INTEGER NOT NULL, `count` INTEGER NOT NULL, PRIMARY KEY(`date`, `actionId`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f7191d9cd986a2239e708d241e55e32a')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `actions`");
        _db.execSQL("DROP TABLE IF EXISTS `day_records`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      public void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      public RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsActions = new HashMap<String, TableInfo.Column>(4);
        _columnsActions.put("actionId", new TableInfo.Column("actionId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActions.put("actionName", new TableInfo.Column("actionName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActions.put("creationTimestamp", new TableInfo.Column("creationTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActions.put("backgroundColor", new TableInfo.Column("backgroundColor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysActions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesActions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoActions = new TableInfo("actions", _columnsActions, _foreignKeysActions, _indicesActions);
        final TableInfo _existingActions = TableInfo.read(_db, "actions");
        if (! _infoActions.equals(_existingActions)) {
          return new RoomOpenHelper.ValidationResult(false, "actions(com.actiontracker.app.models.ActionEntity).\n"
                  + " Expected:\n" + _infoActions + "\n"
                  + " Found:\n" + _existingActions);
        }
        final HashMap<String, TableInfo.Column> _columnsDayRecords = new HashMap<String, TableInfo.Column>(3);
        _columnsDayRecords.put("date", new TableInfo.Column("date", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayRecords.put("actionId", new TableInfo.Column("actionId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayRecords.put("count", new TableInfo.Column("count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDayRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDayRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDayRecords = new TableInfo("day_records", _columnsDayRecords, _foreignKeysDayRecords, _indicesDayRecords);
        final TableInfo _existingDayRecords = TableInfo.read(_db, "day_records");
        if (! _infoDayRecords.equals(_existingDayRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "day_records(com.actiontracker.app.models.DayRecordEntity).\n"
                  + " Expected:\n" + _infoDayRecords + "\n"
                  + " Found:\n" + _existingDayRecords);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f7191d9cd986a2239e708d241e55e32a", "1a6318cdf05c6692d1ed16051ac36ecf");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "actions","day_records");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `actions`");
      _db.execSQL("DELETE FROM `day_records`");
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
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ActionDao.class, ActionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DayRecordDao.class, DayRecordDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  public List<Migration> getAutoMigrations(
      @NonNull Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecsMap) {
    return Arrays.asList();
  }

  @Override
  public ActionDao actionDao() {
    if (_actionDao != null) {
      return _actionDao;
    } else {
      synchronized(this) {
        if(_actionDao == null) {
          _actionDao = new ActionDao_Impl(this);
        }
        return _actionDao;
      }
    }
  }

  @Override
  public DayRecordDao dayRecordDao() {
    if (_dayRecordDao != null) {
      return _dayRecordDao;
    } else {
      synchronized(this) {
        if(_dayRecordDao == null) {
          _dayRecordDao = new DayRecordDao_Impl(this);
        }
        return _dayRecordDao;
      }
    }
  }
}
