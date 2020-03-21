package com.github.sawied.microservice.trade.config;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import org.springframework.integration.mail.SearchTermStrategy;

public class UnseenSearchTermStrategy  implements SearchTermStrategy {
    UnseenSearchTermStrategy(){
        super();
    }
	@Override
	public SearchTerm generateSearchTerm(Flags supportedFlags, Folder folder) {
		 return new FlagTerm(new Flags(Flags.Flag.RECENT), true);
	}
}