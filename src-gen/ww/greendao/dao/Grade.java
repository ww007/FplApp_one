package ww.greendao.dao;

import java.util.List;
import ww.greendao.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table GRADE.
 */
public class Grade {

    private Long GradeID;
    private Long SchoolID;
    private String GradeCode;
    private String GradeName;
    private String Remark1;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient GradeDao myDao;

    private School school;
    private Long school__resolvedKey;

    private List<Classes> allClass;

    public Grade() {
    }

    public Grade(Long GradeID) {
        this.GradeID = GradeID;
    }

    public Grade(Long GradeID, Long SchoolID, String GradeCode, String GradeName, String Remark1) {
        this.GradeID = GradeID;
        this.SchoolID = SchoolID;
        this.GradeCode = GradeCode;
        this.GradeName = GradeName;
        this.Remark1 = Remark1;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getGradeDao() : null;
    }

    public Long getGradeID() {
        return GradeID;
    }

    public void setGradeID(Long GradeID) {
        this.GradeID = GradeID;
    }

    public Long getSchoolID() {
        return SchoolID;
    }

    public void setSchoolID(Long SchoolID) {
        this.SchoolID = SchoolID;
    }

    public String getGradeCode() {
        return GradeCode;
    }

    public void setGradeCode(String GradeCode) {
        this.GradeCode = GradeCode;
    }

    public String getGradeName() {
        return GradeName;
    }

    public void setGradeName(String GradeName) {
        this.GradeName = GradeName;
    }

    public String getRemark1() {
        return Remark1;
    }

    public void setRemark1(String Remark1) {
        this.Remark1 = Remark1;
    }

    /** To-one relationship, resolved on first access. */
    public School getSchool() {
        Long __key = this.SchoolID;
        if (school__resolvedKey == null || !school__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SchoolDao targetDao = daoSession.getSchoolDao();
            School schoolNew = targetDao.load(__key);
            synchronized (this) {
                school = schoolNew;
            	school__resolvedKey = __key;
            }
        }
        return school;
    }

    public void setSchool(School school) {
        synchronized (this) {
            this.school = school;
            SchoolID = school == null ? null : school.getSchoolID();
            school__resolvedKey = SchoolID;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Classes> getAllClass() {
        if (allClass == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ClassesDao targetDao = daoSession.getClassesDao();
            List<Classes> allClassNew = targetDao._queryGrade_AllClass(GradeID);
            synchronized (this) {
                if(allClass == null) {
                    allClass = allClassNew;
                }
            }
        }
        return allClass;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetAllClass() {
        allClass = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
