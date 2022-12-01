package exam.repository;

import exam.model.Shop;
import exam.model.dtos.ShopImportDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByName(String name);

}
