package garden.ephemeral.rocket

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["src/features"],
    tags = "not @ignored"
)
class RunCucumberTest
