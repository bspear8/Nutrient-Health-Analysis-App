package edu.gatech.ihi.nhaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.gatech.ihi.nhaa.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
	User findByUserId(long id);

	@Override
	void delete(User user);
}
