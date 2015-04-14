package net.dasherz.dapenti.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import net.dasherz.dapenti.database.Penti;

import net.dasherz.dapenti.database.PentiDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig pentiDaoConfig;

    private final PentiDao pentiDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        pentiDaoConfig = daoConfigMap.get(PentiDao.class).clone();
        pentiDaoConfig.initIdentityScope(type);

        pentiDao = new PentiDao(pentiDaoConfig, this);

        registerDao(Penti.class, pentiDao);
    }
    
    public void clear() {
        pentiDaoConfig.getIdentityScope().clear();
    }

    public PentiDao getPentiDao() {
        return pentiDao;
    }

}
