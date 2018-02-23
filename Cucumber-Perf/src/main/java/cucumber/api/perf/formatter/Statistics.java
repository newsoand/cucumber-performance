package cucumber.api.perf.formatter;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cucumber.api.Result;
import cucumber.api.perf.result.FeatureResult;
import cucumber.api.perf.result.ScenarioResult;
import cucumber.api.perf.result.StepResult;

public class Statistics {

	private HashMap<String,List<FeatureResult>> results = new HashMap<String,List<FeatureResult>>();
	private HashMap<String,FeatureResult> min = new HashMap<String,FeatureResult>();
	private HashMap<String,FeatureResult> max = new HashMap<String,FeatureResult>();
	private HashMap<String,FeatureResult> avg = new HashMap<String,FeatureResult>();

	public Statistics(List<FeatureResult> results, boolean successOnly, boolean isStrict)
	{
		if (!results.isEmpty())
		{
		for (FeatureResult o : results) {
			if (this.results.containsKey(o.getName())) {
				this.results.get(o.getName()).add(o);
			} else {
				this.results.put(o.getName(), new ArrayList<FeatureResult>(Arrays.asList(o)));
			}
		}
		calculate(successOnly, isStrict);
		}
	}
	
	private void calculate(boolean successOnly, boolean isStrict)
	{
		for (Entry<String,List<FeatureResult>> entry: results.entrySet())
		{
			FeatureResult sum = new FeatureResult(entry.getValue().get(0));
			FeatureResult min = new FeatureResult(entry.getValue().get(0));
			FeatureResult max = new FeatureResult(entry.getValue().get(0));
			boolean first = true;
			for (FeatureResult f : entry.getValue())
			{
				if (!first)
				{
					Result fSum = sum.getResult();
					if ((successOnly && f.getResult().isOk(isStrict)) || !successOnly) {
						sum.setResult(new Result(f.getResult().getStatus(),fSum.getDuration()+f.getResultDuration(),f.getResult().getError()));
						if (f.getResultDuration()>max.getResultDuration())
						{
							max.setResult(new Result(f.getResult().getStatus(),f.getResultDuration(),f.getResult().getError()));
						}
						else if (f.getResultDuration()<min.getResultDuration())
						{
							min.setResult(new Result(f.getResult().getStatus(),f.getResultDuration(),f.getResult().getError()));
						}
					}
					
					for (int sci = 0; sci < f.getChildResults().size(); sci++)
					{
						ScenarioResult sc = f.getChildResults().get(sci);
						if ((successOnly && sc.getResult().isOk(isStrict)) || !successOnly) {
							sum.getChildResults().get(sci).setResult(new Result(sc.getResult().getStatus(),sum.getChildResults().get(sci).getResultDuration()+sc.getResultDuration(),sc.getResult().getError()));
							if (sc.getResultDuration()>max.getChildResults().get(sci).getResultDuration())
							{
								max.getChildResults().get(sci).setResult(new Result(sc.getResult().getStatus(),sc.getResultDuration(),sc.getResult().getError()));
							}
							else if (sc.getResultDuration()<min.getChildResults().get(sci).getResultDuration())
							{
								min.getChildResults().get(sci).setResult(new Result(sc.getResult().getStatus(),sc.getResultDuration(),sc.getResult().getError()));
							}
						}
						for (int sti = 0; sti < sc.getChildResults().size(); sti++)
						{
							StepResult stp = sc.getChildResults().get(sti);
							if (sum.getChildResults().get(sci).getChildResults().get(sti).getResultDuration()!=null&&((successOnly && stp.getResult().isOk(isStrict)) || !successOnly)){
								sum.getChildResults().get(sci).getChildResults().get(sti).setResult(new Result(stp.getResult().getStatus(),sum.getChildResults().get(sci).getChildResults().get(sti).getResultDuration()+stp.getResultDuration(),stp.getResult().getError()));
							}
							if (stp.getResultDuration()!=null && stp.getResultDuration()>max.getChildResults().get(sci).getChildResults().get(sti).getResultDuration())
							{
								max.getChildResults().get(sci).getChildResults().get(sti).setResult(new Result(stp.getResult().getStatus(),stp.getResultDuration(),stp.getResult().getError()));
							}
							else if (stp.getResultDuration()!=null && stp.getResultDuration()<min.getChildResults().get(sci).getChildResults().get(sti).getResultDuration())
							{
								min.getChildResults().get(sci).getChildResults().get(sti).setResult(new Result(stp.getResult().getStatus(),stp.getResultDuration(),stp.getResult().getError()));							}
						}
					}
				}
				else
				{
					first = false;
				}
			}
			FeatureResult avg = new FeatureResult(sum);
			avg.setResult(new Result(avg.getResult().getStatus(),avg.getResultDuration()/entry.getValue().size(),avg.getResult().getError()));
			for (int sci = 0; sci < sum.getChildResults().size(); sci++)
			{
				avg.getChildResults().get(sci).setResult(new Result(sum.getChildResults().get(sci).getResult().getStatus(),sum.getChildResults().get(sci).getResultDuration()/entry.getValue().size(),avg.getChildResults().get(sci).getResult().getError()));
				for (int sti = 0; sti < sum.getChildResults().get(sci).getChildResults().size(); sti++)
				{
					if (avg.getChildResults().get(sci).getChildResults().get(sti).getResult().getDuration()!=null)
					{
					avg.getChildResults().get(sci).getChildResults().get(sti).setResult(new Result(avg.getChildResults().get(sci).getChildResults().get(sti).getResult().getStatus(),avg.getChildResults().get(sci).getChildResults().get(sti).getResultDuration()/entry.getValue().size(),avg.getChildResults().get(sci).getChildResults().get(sti).getResult().getError()));
				
					}
				}
			
			}
			this.avg.put(entry.getKey(), avg);
			this.min.put(entry.getKey(), min);
			this.max.put(entry.getKey(), max);
		}
	}
	
	public HashMap<String,FeatureResult> getAvg()
	{
		return avg;
	}
	public HashMap<String, FeatureResult> getMin() {
		return min;
	}

	public HashMap<String, FeatureResult> getMax() {
		return max;
	}
	
		
}
