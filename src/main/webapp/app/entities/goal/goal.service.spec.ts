import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import GoalService from './goal.service';
import { DATE_FORMAT, DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Goal } from '@/shared/model/goal.model';

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
  describe('Goal Service', () => {
    let service: GoalService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new GoalService();
      currentDate = new Date();
      elemDefault = new Goal(123, 'AAAAAAA', 'AAAAAAA', 'SHORT_TERM', currentDate, false, currentDate, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = {
          deadline: dayjs(currentDate).format(DATE_FORMAT),
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
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

      it('should create a Goal', async () => {
        const returnedFromService = {
          id: 123,
          deadline: dayjs(currentDate).format(DATE_FORMAT),
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { deadline: currentDate, createdAt: currentDate, updatedAt: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Goal', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Goal', async () => {
        const returnedFromService = {
          title: 'BBBBBB',
          description: 'BBBBBB',
          goalType: 'BBBBBB',
          deadline: dayjs(currentDate).format(DATE_FORMAT),
          completed: true,
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };

        const expected = { deadline: currentDate, createdAt: currentDate, updatedAt: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Goal', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Goal', async () => {
        const patchObject = {
          description: 'BBBBBB',
          deadline: dayjs(currentDate).format(DATE_FORMAT),
          completed: true,
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...new Goal(),
        };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { deadline: currentDate, createdAt: currentDate, updatedAt: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Goal', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Goal', async () => {
        const returnedFromService = {
          title: 'BBBBBB',
          description: 'BBBBBB',
          goalType: 'BBBBBB',
          deadline: dayjs(currentDate).format(DATE_FORMAT),
          completed: true,
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updatedAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { deadline: currentDate, createdAt: currentDate, updatedAt: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Goal', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Goal', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Goal', async () => {
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
