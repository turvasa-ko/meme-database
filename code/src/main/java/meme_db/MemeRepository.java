package meme_db;


import org.springframework.data.jpa.repository.JpaRepository;


public interface MemeRepository extends JpaRepository<Meme, String> {}
