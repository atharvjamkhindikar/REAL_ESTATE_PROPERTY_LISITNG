using RealEstate.RecommendationService.Models;


namespace RealEstate.RecommendationService.Services
{
	public class RecommendationService
	{
		public List<Property> Recommend(List<Property> properties, string location, double budget)
		{
			return properties
				.Where(p => p.Location.Equals(location, StringComparison.OrdinalIgnoreCase))
				.Where(p => p.Price >= budget * 0.9 && p.Price <= budget * 1.1)
				.Take(5)
				.ToList();
		}
	}
}
