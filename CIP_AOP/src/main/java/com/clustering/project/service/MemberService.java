package com.clustering.project.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clustering.project.dao.ShareDao;
import com.clustering.project.util.CommonUtil;

@Service
public class MemberService {

	@Autowired
	private ShareDao dao;
	
	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private AuthorityRmemberService authorityRmemberService;

	public List<Object> getList(Object dataMap) {
		String sqlMapId = "member.list";

		List<Object> resultObject = dao.getList(sqlMapId, dataMap);
		
		return resultObject;
	}

	public Object getObject(Object dataMap) {
		String sqlMapId = "member.read";

		Map resultObject = (Map) dao.getObject(sqlMapId, dataMap);
		
		// Get Authorities By Member_seq
		sqlMapId = "authorityRmember.list";
		resultObject.put("authorityList", dao.getList(sqlMapId, dataMap));
		
		// Get Authorities By Member_seq
		sqlMapId = "attachfile.list";
		((Map) dataMap).put("SOURCE_UNIQUE_SEQ", ((Map) dataMap).get("MEMBER_SEQ"));
		resultObject.put("attachFileList", dao.getList(sqlMapId, dataMap));

		return resultObject;
	}

	public Object saveObject(Object dataMap) {
		Map<String, Object> paramMap = (Map<String, Object>) dataMap;
		String uniqueSequence = (String) paramMap.get("MEMBER_SEQ");
		String password = (String) paramMap.get("PASSWORD");
		
		if("".equals(uniqueSequence)){
			uniqueSequence = commonUtil.getUniqueSequence();
		}
		paramMap.put("MEMBER_SEQ", uniqueSequence);
//		paramMap.put("CRYPT_PASSWORD", commonUtil.PasswordEncoderGenerator(password));
		paramMap.put("REGISTER_SEQ", "UUID-1111-1111111");
		paramMap.put("MODIFIER_SEQ", "UUID-1111-1111111");
		
		String sqlMapId = "member.merge";

		Integer resultKey = (Integer) dao.saveObject(sqlMapId, paramMap);

		// insert Authority by Member
		authorityRmemberService.insertObject(paramMap);

		// reading member information
		Map resultObject = (Map) this.getObject(paramMap);

		return resultObject;
	}

	public Object deleteObject(Object dataMap) {
		String sqlMapId = "member.delete";

		Integer resultKey = (Integer) dao.deleteObject(sqlMapId, dataMap);

		sqlMapId = "member.list";
		
		List<Object> resultObject = dao.getList(sqlMapId, dataMap);
		
		return resultObject;
	}
}
