package com.example.choconut.re_markable.qcloud.Module;

public class Morphling extends Base {
	public Morphling(String module) {
		serverHost = module + ".api.qcloud.com";
	}

    public void morph(String module) {
        serverHost = module + ".api.qcloud.com";
    }
}
