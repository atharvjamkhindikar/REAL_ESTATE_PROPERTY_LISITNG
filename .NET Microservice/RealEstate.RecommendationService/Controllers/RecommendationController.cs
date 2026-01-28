using Microsoft.AspNetCore.Mvc;
using RealEstate.RecommendationService.Models;
using RealEstate.RecommendationService.Services;

namespace RealEstate.RecommendationService.Controllers
{
	[ApiController]
	[Route("api/recommend")]
	public class RecommendationController : ControllerBase
	{
		private readonly Services.RecommendationService _service;

		public RecommendationController(Services.RecommendationService service)
		{
			_service = service;
		}

		[HttpPost]
		public IActionResult Recommend([FromBody] RecommendationRequest request)
		{
			var result = _service.Recommend(request.Properties, request.Location, request.Budget);
			return Ok(result);
		}
	}

	public class RecommendationRequest
	{
		public List<Property> Properties { get; set; }
		public string Location { get; set; }
		public double Budget { get; set; }
	}
}
