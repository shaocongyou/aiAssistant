import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import FocusSessionUpdate from './focus-session-update.vue';
import FocusSessionService from './focus-session.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';

type FocusSessionUpdateComponentType = InstanceType<typeof FocusSessionUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const focusSessionSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<FocusSessionUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('FocusSession Management Update Component', () => {
    let comp: FocusSessionUpdateComponentType;
    let focusSessionServiceStub: SinonStubbedInstance<FocusSessionService>;

    beforeEach(() => {
      route = {};
      focusSessionServiceStub = sinon.createStubInstance<FocusSessionService>(FocusSessionService);
      focusSessionServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          focusSessionService: () => focusSessionServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(FocusSessionUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(FocusSessionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.focusSession = focusSessionSample;
        focusSessionServiceStub.update.resolves(focusSessionSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(focusSessionServiceStub.update.calledWith(focusSessionSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        focusSessionServiceStub.create.resolves(entity);
        const wrapper = shallowMount(FocusSessionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.focusSession = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(focusSessionServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        focusSessionServiceStub.find.resolves(focusSessionSample);
        focusSessionServiceStub.retrieve.resolves([focusSessionSample]);

        // WHEN
        route = {
          params: {
            focusSessionId: `${focusSessionSample.id}`,
          },
        };
        const wrapper = shallowMount(FocusSessionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.focusSession).toMatchObject(focusSessionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        focusSessionServiceStub.find.resolves(focusSessionSample);
        const wrapper = shallowMount(FocusSessionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
