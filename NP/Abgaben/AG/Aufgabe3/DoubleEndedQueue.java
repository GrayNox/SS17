import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DoubleEndedQueue {

	Lock lockleft = new ReentrantLock();
	Lock lockright = new ReentrantLock();

	List left = new ArrayList();
	List right = new ArrayList();
	int lenght = 0;

	void addLast(Object addR) {
		lockright.lock();
		try {
			right.add(addR);
			lenght++;
		} finally {
			lockright.unlock();
		}
	}

	void addFirst(Object addL) {
		if (lenght >= 5) {
			lockleft.lock();
			try {
				left.add(addL);
				lenght++;

			} finally {
				lockleft.unlock();
			}
		} else {
			lockright.lock();
			try {
				right.add(addL);
				lenght++;
			} finally {
				lockright.unlock();
			}
		}

	}

	Object removeFirst() {
		Object res = null;
		if (lenght != 0) {
			lockleft.lock();
			try {
				if (left.size() != 0) {
					res = left.get(0);
					left.remove(0);
					lenght--;
				} else {
					lockright.lock();
					try {
						res = right.get(0);
						right.remove(0);
						lenght--;
					} finally {
						lockright.unlock();
					}
				}
			} finally {
				lockleft.unlock();
			}
		} else {
			throw new NoSuchElementException();
		}
		return res;
	}

	Object removeLast() {
		Object res = null;

		if (lenght != 0) {
			lockright.lock();
			try {
				if (right.size() > 0) {
					res = right.get(right.size() - 1);
					right.remove(right.size() - 1);
					lenght--;
					lockleft.lock();
					try {
						if (lenght != 0 && right.size() == 0) {
							right.add(0, left.get(0));
							left.remove(0);
						}
					} finally {
						lockleft.unlock();
					}

				}
			} finally {
				lockright.unlock();
			}
			lockleft.lock();
			try {
				res = left.get(left.size() - 1);
				left.remove(left.size() - 1);
				lenght--;
			} finally {
				lockleft.unlock();
			}
		} else {
			throw new NoSuchElementException();
		}

		return res;
	}
}
