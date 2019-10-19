package ued.udn.vn.core.common.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil  {
    /*class: load session factory tu file config*/

    private static final SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();

    /* create session factory tu file config, */
    private static SessionFactory buildSessionFactory(){
        try{
            return new Configuration().configure().buildSessionFactory();

        }catch(Throwable e){
            System.out.println("Inital session factory fail "+e);
            throw  new ExceptionInInitializerError(e);
        }


    }

    /*get  */
    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }


}
