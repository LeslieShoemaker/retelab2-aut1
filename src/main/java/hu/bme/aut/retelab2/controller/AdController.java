package hu.bme.aut.retelab2.controller;

import hu.bme.aut.retelab2.NoAccessException;
import hu.bme.aut.retelab2.domain.Ad;
import hu.bme.aut.retelab2.repository.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/ads")
public class AdController {
    @Autowired
    private AdRepository adRepository;

    @PostMapping
    public Ad create(@RequestBody Ad ad) {
        ad.setId(null);
        return adRepository.save(ad);
    }

    @GetMapping("/search")
    public List<Ad> searchByPrice(@RequestParam(required = false, defaultValue = "0") int minPrice,
                                  @RequestParam(required = false, defaultValue = "10000000") int maxPrice)
    {
        if(minPrice>maxPrice)
        {
            int tmp=minPrice;
            minPrice=maxPrice;
            maxPrice=tmp;
        }
        return adRepository.searchByPrice(minPrice, maxPrice);
    }

    @PutMapping
    public Ad update(@RequestBody Ad updatedAd) throws NoAccessException
    {
        return adRepository.update(updatedAd);
    }

    @GetMapping("/{tag}")
    public List<Ad> searchByTag(@PathVariable String tag) {
        return adRepository.searchByTag(tag);
    }
}
