package hu.bme.aut.retelab2.repository;

import hu.bme.aut.retelab2.NoAccessException;
import hu.bme.aut.retelab2.SecretGenerator;
import hu.bme.aut.retelab2.domain.Ad;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public class AdRepository {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Ad save(Ad ad) {
        ad.setSecretId(SecretGenerator.generate());
        ad.setCreatedDate();
        return em.merge(ad);
    }

    public List<Ad> searchByPrice(int min, int max) {
        List<Ad> result = em.createQuery("select a from Ad a where a.price between ?1 and ?2", Ad.class).setParameter(1, min).setParameter(2, max).getResultList();

        return deletePrivateId(result);
    }

    @Transactional
    public Ad update(Ad updatedAd) throws NoAccessException
    {
        Ad found = em.find(Ad.class, updatedAd.getId());

        if (!updatedAd.getSecretId().equals(found.getSecretId())) {
            throw new NoAccessException();
        }
        em.remove(found);
        return em.merge(updatedAd);
    }

    public List<Ad> searchByTag(String tag) {
        List<Ad> result = em.createQuery("select a from Ad a where ?1 member a.tags", Ad.class)
                .setParameter(1, tag)
                .getResultList();

        return deletePrivateId(result);
    }

    private List<Ad> deletePrivateId(List<Ad> toBeDeleted)
    {
        toBeDeleted.forEach(ad -> ad.setSecretId(null));
        return toBeDeleted;
    }

    @Scheduled(fixedDelay = 6000)
    @Transactional
    @Modifying
    public void deleteOldEntries() {
        List<Ad> old = em.createQuery("select a from Ad a where a.expiryDate < ?1", Ad.class).setParameter(1, LocalDateTime.now()).getResultList();
        old.forEach(ad -> {
            ad.getTags().clear();
            em.remove(ad);
        });
    }

}
