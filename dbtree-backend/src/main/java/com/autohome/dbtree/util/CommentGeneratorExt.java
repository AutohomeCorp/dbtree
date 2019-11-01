package com.autohome.dbtree.util;

import com.google.common.base.Strings;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentGeneratorExt extends DefaultCommentGenerator {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommentGeneratorExt.class);

    @Override
    public void addFieldComment(Field field,
                                IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        String remarks = introspectedColumn.getRemarks();
        if (Strings.isNullOrEmpty(remarks)) {
            return;
        }
        try {
            field.addJavaDocLine("/**");
            field.addJavaDocLine(" * " + remarks);
            field.addJavaDocLine(" */");
        } catch (Exception ex) {
            LOGGER.error("error when addFieldComment", ex);
        }
    }
}
