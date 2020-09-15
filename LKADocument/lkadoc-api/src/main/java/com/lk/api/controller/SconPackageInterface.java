package com.lk.api.controller;

import java.io.IOException;
import java.util.List;

public interface SconPackageInterface {
	public List<String> getFullyQualifiedClassNameList() throws IOException;
}
