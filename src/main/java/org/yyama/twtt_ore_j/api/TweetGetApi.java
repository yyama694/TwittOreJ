package org.yyama.twtt_ore_j.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TweetGetApi {
	@RequestMapping("/")
	@ResponseBody
	public String getSample() {
		return "aaa";
	}
}
