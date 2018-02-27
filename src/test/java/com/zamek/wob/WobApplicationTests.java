package com.zamek.wob;

import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.Suite;

import com.zamek.wob.domain.CSVTest;
import com.zamek.wob.domain.DbTest;
import com.zamek.wob.domain.DomainTest;


@RunWith(Runner.class)
@Suite.SuiteClasses ({
	DomainTest.class,
	DbTest.class,
	CSVTest.class
})

public class WobApplicationTests {
//NC
}
