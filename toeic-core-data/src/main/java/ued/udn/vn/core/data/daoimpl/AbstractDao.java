package ued.udn.vn.core.data.daoimpl;

import org.hibernate.*;
import ued.udn.vn.core.common.constant.CoreConstant;
import ued.udn.vn.core.common.utils.HibernateUtil;
import ued.udn.vn.core.data.dao.GenericDao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class AbstractDao<ID extends Serializable, T> implements GenericDao<ID, T> {

    //ID trong moi Entity co nhuwng primary key, T: Table dai dien Entity

    private Class<T> persistenceClass;

    public AbstractDao() {
        this.persistenceClass = (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[1];

        //ParameterizedType: get tat ca tham so class AbstractDao <ID extends Serializable, T> , [0]: ID, [1]: T

    }

    public String getPersistenceClassName(){
        return persistenceClass.getSimpleName();  // phuong thuc convert class -> String
    }

/*
    protected Session getSession(){                //cung mot thang cha, hoac packge thi co the su dung duoc
    return HibernateUtil.getSessionFactory().openSession();

    }
    // khi dùng chung session, thí nó dùng session của thằng đầu tiên chạy, do đó nên khai báo dùng riêng chơ từng thằng
*/
    public List findAll() {
        List<T> list= new ArrayList<T>();
        Transaction transaction= null;
        Session session= HibernateUtil.getSessionFactory().openSession();
        try{

            transaction= session.beginTransaction();
            StringBuilder sql= new StringBuilder(" FROM ");
            sql.append(this.getPersistenceClassName());
            Query query= session.createQuery(sql.toString());
            list= query.list();
            transaction.commit();
        } catch (HibernateException e){
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

        return list;
    }

    public T update(T entity){
        T result= null;
        Session session= HibernateUtil.getSessionFactory().openSession();
        Transaction transaction= session.beginTransaction();
        try{
            Object object=  session.merge(entity);
            result =(T) object;
            transaction.commit();
        }catch (HibernateException e){
            transaction.rollback();
        throw e;
        }
        finally {
            session.close();
        }
        return result;
    }

    public void save(T entity) {
        // dang ki, dang nhap
        T result= null;
        Session session= HibernateUtil.getSessionFactory().openSession();
        Transaction transaction= session.beginTransaction();
        try{
            session.persist(entity);
            transaction.commit();
        }catch (HibernateException e){
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public T findById(ID id) {
       T result = null;
       Session session= HibernateUtil.getSessionFactory().openSession();
       Transaction transaction= session.beginTransaction();
        try{
            result = (T) session.get(persistenceClass, id);
            if(result==null){
                throw new ObjectNotFoundException("Not found object role by id = "+id, null);
            }

        }catch (HibernateException e){
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }

       return result;
    }

    public Object[] findByProperty(String property, Object value, String sortExpression, String sortDirection) {
        List<T> list = new ArrayList<T>();
        Session session= HibernateUtil.getSessionFactory().openSession();
        Transaction transaction= session.beginTransaction();
        Object totalItem =0;
        try{
            StringBuilder sql1= new StringBuilder(" FROM ");
            sql1.append(getPersistenceClassName());
            /*show all*/
            if(property != null && value != null){
                sql1.append(" WHERE ").append(property).append(" = :value" );

            }
            if(sortDirection != null && sortExpression != null){
                // asc desc
                //
                //sortDirection.equalsIgnoreCase("ASC") ? "ASC" : "DESC";
                sql1.append(" ORDER BY ").append(sortExpression);
                sql1.append(" "+ (sortDirection.equals(CoreConstant.SORT_ASC)?"asc":"desc"));

            }
            org.hibernate.query.Query query1 = session.createQuery(sql1.toString());

            if(value!= null){
                query1.setParameter("value", value);
            }
            list = query1.list();

            StringBuilder sql2 = new StringBuilder("SELECT COUNT(*) FROM ");
            sql2.append(getPersistenceClassName());
            if(property!= null && value !=null){
                sql2.append(" WHERE ").append(property).append(" =:value ");

            }
            org.hibernate.query.Query query2= session.createQuery(sql2.toString());
            if(value!=null){
                query2.setParameter("value", value);
            }

            totalItem = query2.list().get(0);


            sql1.append(" ");

            transaction.commit();
        }catch (HibernateException e){
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
        return new Object[]{totalItem, list};

    }

    public Integer delete(List<ID> ids) {
        Integer count = 0;
        Session session= HibernateUtil.getSessionFactory().openSession();
        Transaction transaction= session.beginTransaction();
        try{
            for(ID item: ids){
                T t = (T) session.get(persistenceClass, item);
                session.delete(t);
                count++;
            }
            transaction.commit();
        }catch (HibernateException e){
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }


        return null;
    }


}
