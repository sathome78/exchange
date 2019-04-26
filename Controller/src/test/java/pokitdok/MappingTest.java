package pokitdok;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import service.api.pokitdok.model.claimStatus.RootClaimStatus;
import service.api.pokitdok.model.eligibility.RootEligibility;
import service.api.pokitdok.model.mpc.RootMPCs;
import service.api.pokitdok.model.partner.RootTradingPartners;
import service.api.pokitdok.model.provider.RootProviders;
import persistence.json.JsonTransformer;
import persistence.json.JsonTransformerImpl;

import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MappingTest extends BaseComparatorTest {

    JsonTransformer jsonTransformer = new JsonTransformerImpl();

    private final Class<?> cls;

    public MappingTest(String in, String type) throws Exception {
        super(in, in);
        this.cls = Class.forName(type);
    }

    @Parameterized.Parameters(name = " {index}. IN: {0} ")
    public static Collection<Object[]> data() {
        return new ArrayList<Object[]>() {
            {
                add(new Object[]{"pokitdok/providers.json", RootProviders.class.getName()});
                add(new Object[]{"pokitdok/providers2.json", RootProviders.class.getName()});

                add(new Object[]{"pokitdok/eligibility.json", RootEligibility.class.getName()});
                add(new Object[]{"pokitdok/eligibility2.json", RootEligibility.class.getName()});

                add(new Object[]{"pokitdok/mpcs.json", RootMPCs.class.getName()});
                add(new Object[]{"pokitdok/traiding_partners.json", RootTradingPartners.class.getName()});

                add(new Object[]{"pokitdok/claimStatus.json", RootClaimStatus.class.getName()});
                add(new Object[]{"pokitdok/claimStatus2.json", RootClaimStatus.class.getName()});
                add(new Object[]{"pokitdok/claimStatus3.json", RootClaimStatus.class.getName()});
                add(new Object[]{"pokitdok/claimStatus4.json", RootClaimStatus.class.getName()});
                add(new Object[]{"pokitdok/claimStatus5.json", RootClaimStatus.class.getName()});
                add(new Object[]{"pokitdok/claimStatus6.json", RootClaimStatus.class.getName()});
            }
        };
    }

    @Override
    protected Object applyTransformation() throws Exception {
        return jsonTransformer.parse(getInput(), cls);
    }

}