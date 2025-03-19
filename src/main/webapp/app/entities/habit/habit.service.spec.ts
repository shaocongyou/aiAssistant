import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import HabitService from './habit.service';
import { DATE_FORMAT } from '@/shared/composables/date-format';
import { Habit } from '@/shared/model/habit.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Habit Service', () => {
    let service: HabitService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new HabitService();
      currentDate = new Date();
      elemDefault = new Habit(123, 'AAAAAAA', 'HEALTH', 'AAAAAAA', currentDate, false);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = { startDate: dayjs(currentDate).format(DATE_FORMAT), ...elemDefault };
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a Habit', async () => {
        const returnedFromService = { id: 123, startDate: dayjs(currentDate).format(DATE_FORMAT), ...elemDefault };
        const expected = { startDate: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Habit', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Habit', async () => {
        const returnedFromService = {
          name: 'BBBBBB',
          habitType: 'BBBBBB',
          frequency: 'BBBBBB',
          startDate: dayjs(currentDate).format(DATE_FORMAT),
          active: true,
          ...elemDefault,
        };

        const expected = { startDate: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Habit', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Habit', async () => {
        const patchObject = { name: 'BBBBBB', habitType: 'BBBBBB', frequency: 'BBBBBB', active: true, ...new Habit() };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { startDate: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Habit', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Habit', async () => {
        const returnedFromService = {
          name: 'BBBBBB',
          habitType: 'BBBBBB',
          frequency: 'BBBBBB',
          startDate: dayjs(currentDate).format(DATE_FORMAT),
          active: true,
          ...elemDefault,
        };
        const expected = { startDate: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Habit', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Habit', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Habit', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
