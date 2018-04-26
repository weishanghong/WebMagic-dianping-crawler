package com.weiresearch.formatter;

import us.codecraft.webmagic.model.formatter.ObjectFormatter;

public class StringTemplateFormatter implements ObjectFormatter<String> {
	private String template;

	@Override
	public String format(String raw) throws Exception {
		// TODO Auto-generated method stub
		return String.format(template, raw);
	}

	@Override
	public Class<String> clazz() {
		// TODO Auto-generated method stub
		return String.class;
	}

	@Override
	public void initParam(String[] extra) {
		// TODO Auto-generated method stub
		template = extra[0];
	}
}
