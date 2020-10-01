package inc.roms.rcs.service.issue.domain.repository;

import inc.roms.rcs.service.issue.domain.model.Issue;
import inc.roms.rcs.vo.issue.IssueId;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssueRepository extends CrudRepository<Issue, Integer>, JpaSpecificationExecutor<Issue> {

    Optional<Issue> findByIssueId(IssueId issueId);
}
