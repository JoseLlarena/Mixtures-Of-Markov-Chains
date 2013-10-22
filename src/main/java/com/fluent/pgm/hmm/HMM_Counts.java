package com.fluent.pgm.hmm;

import com.fluent.collections.FMap;
import com.fluent.math.*;
import com.fluent.pgm.WeightMap;

import static com.fluent.collections.Maps.newFMap;
import static com.fluent.core.oo.*;
import static com.fluent.pgm.Sequence.*;
import static java.lang.String.format;
import static java.util.Objects.hash;

public class HMM_Counts
{
    public WeightMap transitions;
    public WeightMap emissions;
    public FMap<String, Double> allStates;

    public HMM_Counts()
    {
        transitions = new WeightMap();
        emissions = new WeightMap();
        emissions.add(oo(END_STATE, END), 1.);
        allStates = newFMap(oo(START_STATE, 0.), oo(END_STATE, 1.));
    }

    public HMM_Counts add(final HMM_Counts update)
    {
        update.transitions.entries().each(entry -> addTransition(entry.$1, entry.$2, entry.$3));
        update.emissions.entries().each(entry -> addEmission(entry.$1, entry.$2, entry.$3));
        update.allStates.each((key,value) -> addState(key,value));

        return this;
    }

    public HMM_Counts addEmission(final String Sj, final String Ot, final double p)
    {
        if (p != 0 && Sj != END_STATE && Ot != END)
        {
            emissions.add(Sj, Ot, emissions.get(Sj, Ot, 0.) + p);
        }

        return this;
    }

    public HMM_Counts addEmission(final String Si, final String Sj, final P p)
    {
        return addEmission(Si, Sj, p.toDouble());
    }

    public HMM_Counts addState(final String Si, final double p)
    {
        if (p != 0 && Si != END_STATE)
        {
            allStates.plus(Si, allStates.get(Si, 0.) + p);
        }

        return this;
    }

    public HMM_Counts addState(final String Si, final P p)
    {
        return addState(Si, p.toDouble());
    }

    public HMM_Counts addTransition(final String Si, final String Sj, final double p)
    {
        if (p != 0)
        {
            transitions.add(Si, Sj, transitions.get(Si, Sj, 0.) + p);
            if (Si == START_STATE)
                allStates.plus(START_STATE, allStates.get(START_STATE, 0.) + p * .5);
        }

        return this;
    }

    public HMM_Counts addTransition(final String Si, final String Sj, final P p)
    {
        return addTransition(Si, Sj, p.toDouble());
    }

    public FMap<String, Double> allStates()
    {
        return allStates;
    }

    public WeightMap emissions()
    {
        return emissions;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final HMM_Counts other = (HMM_Counts) obj;
        if (allStates == null)
        {
            if (other.allStates != null)
            {
                return false;
            }
        } else if (!allStates.equals(other.allStates))
        {
            return false;
        }
        if (emissions == null)
        {
            if (other.emissions != null)
            {
                return false;
            }
        } else if (!emissions.equals(other.emissions))
        {
            return false;
        }
        if (transitions == null)
        {
            if (other.transitions != null)
            {
                return false;
            }
        } else if (!transitions.equals(other.transitions))
        {
            return false;
        }
        return true;
    }

    public int hashCode()
    {
        return hash(transitions, emissions, allStates);
    }

    public String toString()
    {
        return format("%n(Si,Sj)%n%s%n(Sj,O(t))%n%s%n(S)%n%s", transitions, emissions, allStates);
    }

    public WeightMap transitions()
    {
        return transitions;
    }
}