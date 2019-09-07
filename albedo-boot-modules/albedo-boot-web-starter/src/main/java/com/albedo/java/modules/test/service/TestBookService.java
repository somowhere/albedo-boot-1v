/**
 * Copyright &copy; 2018 <a href="https://github.com/somewhereMrli/albedo-boot">albedo-boot</a> All rights reserved.
 */
package com.albedo.java.modules.test.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albedo.java.common.persistence.service.DataVoService;
import com.albedo.java.modules.test.domain.TestBook;
import com.albedo.java.modules.test.domain.vo.TestBookVo;
import com.albedo.java.modules.test.repository.TestBookRepository;

/**
 * 测试书籍Service 测试书籍
 * @author admin
 * @version 2018-03-21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestBookService extends DataVoService<TestBookRepository, TestBook, String, TestBookVo>{

}