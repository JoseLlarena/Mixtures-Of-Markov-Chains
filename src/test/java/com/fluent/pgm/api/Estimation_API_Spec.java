package com.fluent.pgm.api;

import com.fluent.collections.FList;
import com.fluent.pgm.Sequence;
import com.fluent.pgm.estimation.Optimiser;
import com.fluent.pgm.hmm.HMM;
import com.fluent.pgm.hmm.HMM_EM;
import com.fluent.pgm.hmm.HMM_Initialiser;
import com.fluent.pgm.momc.Mixture;
import com.fluent.pgm.momc.MixtureEstimation;
import com.fluent.pgm.momc.MixtureInitialiser;
import com.fluent.specs.unit.AbstractSpec;
import org.junit.Before;
import org.mockito.Mock;

@SuppressWarnings("unchecked")
public class Estimation_API_Spec extends AbstractSpec
{
	Estimation_API api;

	@Mock MixtureInitialiser initialiser;
	@Mock Optimiser optimiser;

	@Mock MixtureEstimation.Context mixtureContext;

	@Mock Mixture mixture, newMixture;

    @Mock HMM_Initialiser hmmInitialiser;
    @Mock FList<Sequence> data;
    @Mock HMM_EM.Context context;
    @Mock Optimiser hmmOptimiser;

    @Mock HMM hmm, newHmm;

    @Before
    public void BACKGROUND()
    {
        api = new Estimation_API(hmmInitialiser, hmmOptimiser);
    }

//    @Test public void behaviour_verification_creates_hmm_from_data() throws Exception
//    {
//        IF(hmmInitialiser.init(data, context)).RETURNS(hmm);
//
//        CALLING(api.hmmFrom(data, context));
//
//        WILL_CALL(hmmInitialiser).init(data, context);
//        AND_ALSO(hmmOptimiser).optimise(Matchers.any(F3.class), eq(hmm), eq(data), eq(context));
//    }
//
//    @Test public void creates_hmm_from_data() throws Exception
//    {
//        IF(hmmInitialiser.init(data, context)).RETURNS(hmm)
//                .AND(hmmOptimiser.optimise(Matchers.any(F3.class), eq(hmm), eq(data), eq(context))).RETURNS(newHmm);
//
//        THE_OUTPUT_OF(api.hmmFrom(data, context)).shouldBe(newHmm);
//    }
//
//	@Test public void creates_mixture_from_data() throws Exception
//	{
//		GIVEN(initialiser.init(data, mixtureContext)).RETURNS(mixture);
//		GIVEN(optimiser.optimise(Matchers.any(F3.class), eq(mixture), eq(data), eq(context))).RETURNS(oo(newMixture, ZERO));
//
//		WHEN(new Estimation_API(initialiser, optimiser).mixtureFrom(data, mixtureContext));
//
//		THEN(initialiser).should().init(data, mixtureContext);
//		AND(optimiser).should().optimise(Matchers.any(F3.class), eq(mixture), eq(data), eq(context));
//		AND(theOutcome).shouldBe(newMixture);
//
//	}
}
