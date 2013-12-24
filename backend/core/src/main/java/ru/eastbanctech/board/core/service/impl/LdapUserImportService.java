package ru.eastbanctech.board.core.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.eastbanctech.board.core.model.User;
import ru.eastbanctech.board.core.service.ErrorType;
import ru.eastbanctech.board.core.service.ILdapUserImportService;
import ru.eastbanctech.board.core.service.IUserService;
import ru.eastbanctech.board.core.service.ServiceException;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: y.bulkin
 */
@Service
@Transactional
public class LdapUserImportService implements ILdapUserImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserImportService.class);

    @Inject
    private Environment environment;

    @Autowired
    private IUserService userService;

    @Override
    public int importUsers() throws ServiceException {

        Collection<Map<String, String>> ldapAttributes = fetchAttributes();
        int counter = 0;
        if (CollectionUtils.isNotEmpty(ldapAttributes)) {
            for (Map<String, String> userMap : ldapAttributes) {
                if (MapUtils.isNotEmpty(userMap)) {
                    User user = new User();
                    for (Map.Entry<String, String> attributeEntry : userMap.entrySet()) {
                        String attributeName = attributeEntry.getKey();
                        String attributeValue = attributeEntry.getValue();
                        switch (attributeName) {
                            case "sAMAccountName":
                                user.setLogin(attributeValue);
                                break;
                            case "givenName":
                                user.setFirstName(attributeValue);
                                break;
                            case "sn":
                                user.setLastName(attributeValue);
                                break;
                            case "mail":
                                user.setEmail(attributeValue);
                                break;
                            case "telephoneNumber":
                                user.setPhone(attributeValue);
                                break;
                        }
                    }
                    userService.createOrUpdate(user);
                    counter++;
                }
            }
        }
        return counter;
    }

    private Collection<Map<String, String>> fetchAttributes() throws ServiceException {

        try {
            NamingEnumeration<SearchResult> answer = getLdapSearchResult(getLdapContext());
            Collection<Map<String, String>> usersAttributes = Lists.newArrayList();
            while (answer.hasMore()) {
                Map<String, String> attributesMap = Maps.newHashMap();
                SearchResult result = answer.next();
                BasicAttributes attribs = (BasicAttributes)result.getAttributes();

                NamingEnumeration<Attribute> values = attribs.getAll();
                while (values.hasMore()) {
                    Attribute attribute = values.next();

                    ArrayList<Attribute> all = Collections.list((NamingEnumeration<Attribute>)attribute.getAll());

                    attributesMap.put(attribute.getID(), Joiner.on(";").join(
                            Lists.transform(all, new Function<Object, String>() {
                                @Override
                                public String apply(Object o) {

                                    return o.toString();
                                }
                            })));
                }
                if (MapUtils.isNotEmpty(attributesMap)) {
                    LOGGER.debug("attributesMap = {}", attributesMap);
                    usersAttributes.add(attributesMap);
                }
            }

            return usersAttributes;

        } catch (NamingException e) {
            LOGGER.error("Error: ", e);
            throw new ServiceException(ErrorType.LDAP, e);
        }
    }

    private NamingEnumeration<SearchResult> getLdapSearchResult(LdapContext ctx) throws NamingException {


        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attrIDs = environment.getProperty("ldap.attributes").split(",");

        constraints.setReturningAttributes(attrIDs);
        return ctx.search(environment.getProperty("ldap.base.dn"), environment.getProperty("ldap.domain.user.filter"),
                constraints);
    }

    private LdapContext getLdapContext() {

        LdapContext ctx = null;
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");
            env.put(Context.SECURITY_PRINCIPAL, environment.getProperty("ldap.manager.dn"));
            env.put(Context.SECURITY_CREDENTIALS, environment.getProperty("ldap.manager.password"));
            env.put(Context.PROVIDER_URL, environment.getProperty("ldap.url"));
            ctx = new InitialLdapContext(env, null);
            System.out.println("Connection Successful.");
        } catch (NamingException nex) {
            System.out.println("LDAP Connection: FAILED");
            nex.printStackTrace();
        }
        return ctx;
    }
}
