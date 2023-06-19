package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select case when count(b) > 0 then true else false END " +
            "from Booking as b " +
            "join b.user as u " +
            "where u.id = ?1 and b.id = ?2")
    boolean isBooker(long userId, long bookingId);

    @Query(value = "select case when count(b) > 0 then true else false END " +
            "from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.id = ?1 and b.id = ?2")
    boolean isOwner(long ownerId, long bookingId);

    @Query(value = "select b from Booking as b " +
            "join b.user as u " +
            "where u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByUser(long userId, Pageable pageable);

    @Query(value = "select b from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwner(long ownerId, Pageable pageable);

    @Query(value = "select b from Booking as b " +
            "join b.user as u " +
            "where u.id = ?1 and " +
            "(b.start <= ?2 and b.end >= ?2) " +
            "order by b.start desc ")
    List<Booking> findAllCurrentBookingsByUser(long userId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.id = ?1 and " +
            "(b.start <= ?2 and b.end >= ?2) " +
            "order by b.start desc")
    List<Booking> findAllCurrentBookingsByOwner(long userId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking as b " +
            "join b.user as u " +
            "where u.id = ?1 and " +
            "b.start > ?2 " +
            "order by b.start desc ")
    List<Booking> findAllFutureBookingsByUser(long userId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.id = ?1 and " +
            "b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findAllFutureBookingsByOwner(long userId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking as b " +
            "join b.user as u " +
            "where u.id = ?1 and " +
            "b.end < ?2 " +
            "order by b.start desc ")
    List<Booking> findAllPastBookingsByUser(long userId, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "select b from Booking as b " +
            "join b.item as i " +
            "join i.user as u " +
            "where u.id = ?1 and " +
            "b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findAllPastBookingsByOwner(long userId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByUserIdIsAndStatusIsOrderByStartDesc(long userId, Booking.Status status, Pageable pageable);

    List<Booking> findAllByItemUserIdAndStatusIsOrderByStartDesc(long userId, Booking.Status status, Pageable pageable);

    @Query(value = "select * from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "where i.id = ? and b.start_time < now() and b.status = 'APPROVED' " +
            "order by b.end_time desc " +
            "limit 1", nativeQuery = true)
    Booking findLastBooking(long itemId);

    @Query(value = "select * from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "where i.id = ? and b.start_time > now() and b.status = 'APPROVED' " +
            "order by b.start_time asc " +
            "limit 1", nativeQuery = true)
    Booking findNextBooking(long itemId);

    @Query(value = "select b from Booking as b " +
            "join b.item as i " +
            "where i.id in ?1 " +
            "order by b.start asc")
    List<Booking> findAllByItemsId(List<Long> itemsId);
}
